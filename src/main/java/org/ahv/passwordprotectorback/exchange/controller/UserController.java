package org.ahv.passwordprotectorback.exchange.controller;

import io.jsonwebtoken.Claims;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ahv.passwordprotectorback.exchange.controller.other.ControllerAdapter;
import org.ahv.passwordprotectorback.exchange.controller.other.GlobalController;
import org.ahv.passwordprotectorback.exchange.request.user.UpdatePasswordRequest;
import org.ahv.passwordprotectorback.exchange.request.user.UserConnectRequest;
import org.ahv.passwordprotectorback.exchange.request.user.UserRequest;
import org.ahv.passwordprotectorback.exchange.request.user.UserUpdateRequest;
import org.ahv.passwordprotectorback.exchange.response.BasicResponse;
import org.ahv.passwordprotectorback.exchange.response.user.BasicUserResponse;
import org.ahv.passwordprotectorback.exchange.response.user.UserResponse;
import org.ahv.passwordprotectorback.model.Token;
import org.ahv.passwordprotectorback.model.TokenType;
import org.ahv.passwordprotectorback.model.User;
import org.ahv.passwordprotectorback.security.AESUtil;
import org.ahv.passwordprotectorback.security.JWTService;
import org.ahv.passwordprotectorback.service.*;
import org.ahv.passwordprotectorback.validator.NameValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Arrays;
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
    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;
    private final AESUtil aesUtil;

    private ControllerAdapter adapter;
    private NameValidator nameValidator;

    private String refreshToken = null;

    @PostConstruct
    public void init() {
        this.adapter = new ControllerAdapter(elementService, passwordService, typeService, userService);
        this.nameValidator = NameValidator.getInstance();
    }

    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<BasicUserResponse>> getUsers() {
        return getResponse(userService.findAll().stream().map(adapter::convertToBasicUserResponse).toList());
    }

    @GetMapping("/user/id/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<UserResponse> getUserByID(@PathVariable String id) {
        return getResponse(adapter.convertToUserResponse(userService.findObjectByID(id)));
    }

    @GetMapping("/user/name/{username}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<UserResponse> getUserByName(@PathVariable String username) {
        return getResponse(adapter.convertToUserResponse(userService.findByUsername(username)));
    }


    @GetMapping("/user/email/{email}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {
        return getResponse(adapter.convertToUserResponse(userService.findByEmail(email)));
    }

    @PostMapping("/user/login")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<BasicResponse> login(@Valid @RequestBody UserConnectRequest userRequest) {
        Authentication authentication = this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userRequest.getUsername(), userRequest.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userService.findByUsername(userRequest.getUsername());

        return generateNewTokens(user);
    }

    @PostMapping("/user/refreshToken")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<BasicResponse> refreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        String userID = null;

        if (cookies != null) {
            refreshToken = Arrays.stream(cookies)
                    .filter(cookie -> cookie.getName().equals("refreshToken"))
                    .findFirst()
                    .map(Cookie::getValue)
                    .orElse(null);

            userID = Arrays.stream(cookies)
                    .filter(cookie -> cookie.getName().equals("userID"))
                    .findFirst()
                    .map(cookie -> aesUtil.decrypt(cookie.getValue()))
                    .orElse(null);
        }

        if (refreshToken == null || userID == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request");
        }

        User user = userService.findObjectByID(userID);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request");
        }

        List<Token> tokens = tokenService.findAllByUserID(user.getId());
        List<Token> validTokens = tokens.stream()
                .filter(t -> t.getToken().equals(refreshToken))
                .toList();

        if (validTokens.isEmpty()) {
            //TODO: Ban user ?
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request");
        }

        Token refreshTokenRequest = validTokens.getFirst();
        if (refreshTokenRequest.isAlreadyUsed()) {
            //TODO: Ban user ?
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request");
        }

        if (refreshTokenRequest.getExpirationDate().before(new Date())) {
            //TODO: Ban user ?
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request");
        }

        String username = jwtService.extractClaim(refreshToken, true, Claims::getSubject);
        if (!username.equals(user.getUsername())) {
            //TODO : Ban user ?
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request");
        }

        return generateNewTokens(user);
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

            return BasicResponse.builder().message("Updated").build();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
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
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
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

    private ResponseEntity<BasicResponse> generateNewTokens(User user) {
        tokenService.findAllByUserID(user.getId())
                .stream()
                .filter(t -> !t.isAlreadyUsed())
                .forEach(tokenService::delete);

        String token = jwtService.generateToken(user.getUsername());
        String refreshToken = jwtService.generateRefreshToken(user.getUsername());

        saveToken(user.getId(), token, TokenType.TOKEN);
        saveToken(user.getId(), refreshToken, TokenType.REFRESH_TOKEN);

        String[] headers = new String[3];

        headers[0] = createCookie("token", token, 300).toString();
        headers[1] = createCookie("refreshToken", refreshToken, 600).toString();
        headers[2] = createCookie("userID", aesUtil.encrypt(user.getId()), 600).toString();

        return ResponseEntity.ok()
                .header("Set-Cookie", headers)
                .body(BasicResponse.builder().message("User connected").build());
    }

    private void saveToken(String userID, String token, TokenType type) {
        Date expirationDate = jwtService.extractClaim(token, type.equals(TokenType.REFRESH_TOKEN), Claims::getExpiration);

        Token saveToken = Token.builder()
                .token(token)
                .userID(userID)
                .type(type)
                .expirationDate(expirationDate)
                .build();

        tokenService.save(saveToken);
    }

    private ResponseCookie createCookie(String name, String value, int maxAge) {
        return ResponseCookie.from(name, value)
                .maxAge(maxAge)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .build();
    }
}