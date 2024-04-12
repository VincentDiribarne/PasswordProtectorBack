package org.ahv.passwordprotectorback.exchange.controller;

import io.jsonwebtoken.Claims;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ahv.passwordprotectorback.exchange.controller.other.ControllerAdapter;
import org.ahv.passwordprotectorback.exchange.controller.other.GlobalController;
import org.ahv.passwordprotectorback.exchange.request.user.*;
import org.ahv.passwordprotectorback.exchange.response.BasicResponse;
import org.ahv.passwordprotectorback.exchange.response.password.TokenResponse;
import org.ahv.passwordprotectorback.exchange.response.user.BasicUserResponse;
import org.ahv.passwordprotectorback.exchange.response.user.UserResponse;
import org.ahv.passwordprotectorback.model.Token;
import org.ahv.passwordprotectorback.model.User;
import org.ahv.passwordprotectorback.repository.TokenRepository;
import org.ahv.passwordprotectorback.security.JWTService;
import org.ahv.passwordprotectorback.service.ElementService;
import org.ahv.passwordprotectorback.service.PasswordService;
import org.ahv.passwordprotectorback.service.TypeService;
import org.ahv.passwordprotectorback.service.UserService;
import org.ahv.passwordprotectorback.validator.NameValidator;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController extends GlobalController<User> {
    private final UserService userService;
    private final ElementService elementService;
    private final PasswordService passwordService;
    private final TypeService typeService;
    private final JWTService jwtService;
    private final TokenRepository tokenRepository;
    private final AuthenticationManager authenticationManager;

    private ControllerAdapter adapter;
    private NameValidator nameValidator;

    @PostConstruct
    public void init() {
        this.adapter = new ControllerAdapter(elementService, passwordService, typeService, userService);
        this.nameValidator = NameValidator.getInstance();
    }


    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    public List<BasicUserResponse> getUsers() {
        return userService.findAll().stream().map(adapter::convertToBasicUserResponse).toList();
    }

    @GetMapping("/user/id/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse getUserByID(@PathVariable String id) {
        return adapter.convertToUserResponse(userService.findObjectByID(id));
    }

    @GetMapping("/user/name/{username}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse getUserByName(@PathVariable String username) {
        return adapter.convertToUserResponse(userService.findByUsername(username));
    }


    @GetMapping("/user/email/{email}")
    @ResponseStatus(HttpStatus.OK)
    @RolesAllowed("ADMIN")
    public UserResponse getUserByEmail(@PathVariable String email) {
        return adapter.convertToUserResponse(userService.findByEmail(email));
    }


    @PostMapping("/user/login")
    @ResponseStatus(HttpStatus.OK)
    public TokenResponse login(@Valid @RequestBody UserConnectRequest userRequest) {
        Authentication authentication = this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userRequest.getUsername(), userRequest.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtService.generateToken(userRequest.getUsername());
        String refreshToken = jwtService.generateRefreshToken(userRequest.getUsername());

        saveToken(userRequest.getUsername(), refreshToken);

        return TokenResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .build();
    }

    private void saveToken(String username, String refreshToken) {
        User user = userService.findByUsername(username);

        Date expirationDate = jwtService.extractClaim(refreshToken, true, Claims::getExpiration);
        LocalDate expirationLocalDate = expirationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        Token saveToken = Token.builder()
                .token(refreshToken)
                .userID(user.getId())
                .expirationDate(expirationLocalDate)
                .build();

        tokenRepository.save(saveToken);
    }

    @PostMapping("/user/refreshToken")
    @ResponseStatus(HttpStatus.OK)
    public TokenResponse refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        if (refreshTokenRequest.getId() == null || refreshTokenRequest.getToken() == null || refreshTokenRequest.getRefreshToken() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request");
        }

        User user = userService.findObjectByID(refreshTokenRequest.getId());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request");
        }

        List<Token> tokens = tokenRepository.findAllByUserID(user.getId());
        List<Token> validTokens = tokens.stream()
                .filter(t -> t.getToken().equals(refreshTokenRequest.getRefreshToken()))
                .toList();

        if (validTokens.isEmpty()) {
            //TODO: Ban user ?
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request");
        }

        Token refreshToken = validTokens.getFirst();
        if (refreshToken.isAlreadyUsed()) {
            //TODO: Ban user ?
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request");
        }

        if (refreshToken.getExpirationDate().isBefore(LocalDate.now())) {
            //TODO: Ban user ?
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request");
        }

        String username = jwtService.extractClaim(refreshTokenRequest.getRefreshToken(), true, Claims::getSubject);
        if (!username.equals(user.getUsername())) {
            //TODO : Ban user ?
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request");
        }

        return saveAndGenerateNewToken(refreshToken, username);
    }

    private TokenResponse saveAndGenerateNewToken(Token token, String username) {
        token.setAlreadyUsed(true);
        tokenRepository.save(token);

        String newToken = jwtService.generateToken(username);
        String refreshToken = jwtService.generateRefreshToken(username);

        saveToken(username, refreshToken);

        return TokenResponse.builder()
                .token(newToken)
                .refreshToken(refreshToken)
                .build();
    }


    @PostMapping("/user/createAccount")
    @ResponseStatus(HttpStatus.CREATED)
    public BasicResponse saveUser(@Valid @RequestBody UserRequest userRequest) {
        verification(userRequest, null, null);

        return save(userService, adapter.convertToUser(userRequest));
    }


    @PutMapping("/user/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BasicResponse updateUser(@PathVariable String id, @Valid @RequestBody UserUpdateRequest user) {
        User userToUpdate = userService.findObjectByID(id);

        if (userToUpdate != null) {
            verification(user, userToUpdate.getUsername(), userToUpdate.getEmail());

            userToUpdate.setFirstName(user.getFirstName());
            userToUpdate.setLastName(user.getLastName());
            userToUpdate.setUsername(user.getUsername());
            userToUpdate.setEmail(user.getEmail());
            userToUpdate.setModificationDate(LocalDate.now());
            userService.save(userToUpdate);

            return BasicResponse.builder().message("User updated").build();
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found");
        }
    }

    @PutMapping("/user/password/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BasicResponse updatePassword(@PathVariable String id, @Valid @RequestBody UpdatePasswordRequest password) {
        User userToUpdate = userService.findObjectByID(id);

        if (userToUpdate != null) {
            userToUpdate.setPassword(password.getPassword());
            return save(userService, userToUpdate);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found");
        }
    }


    @DeleteMapping("/user/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BasicResponse deleteUser(@PathVariable String id) {
        elementService.findAllByUserID(id).forEach(element -> {
            passwordService.findAllByElementId(element.getId()).forEach(passwordService::delete);
            elementService.delete(element);
        });

        typeService.findAllByUserId(id).forEach(typeService::delete);

        return delete(userService, id);
    }

    //Method
    private void verification(UserUpdateRequest request, String oldUsername, String oldEmail) {
        if (nameValidator.isNotValid(userService.findAllUsernames(), oldUsername, request.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already exists");
        }

        if (nameValidator.isNotValid(userService.findAllEmails(), oldEmail, request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
        }
    }
}