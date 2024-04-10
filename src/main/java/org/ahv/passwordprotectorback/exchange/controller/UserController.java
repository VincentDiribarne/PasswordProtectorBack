package org.ahv.passwordprotectorback.exchange.controller;

import jakarta.validation.Valid;
import org.ahv.passwordprotectorback.exchange.controller.other.ControllerAdapter;
import org.ahv.passwordprotectorback.exchange.controller.other.GlobalController;
import org.ahv.passwordprotectorback.exchange.request.user.UpdatePasswordRequest;
import org.ahv.passwordprotectorback.exchange.request.user.UserConnectRequest;
import org.ahv.passwordprotectorback.exchange.request.user.UserRequest;
import org.ahv.passwordprotectorback.exchange.request.user.UserUpdateRequest;
import org.ahv.passwordprotectorback.exchange.response.BasicResponse;
import org.ahv.passwordprotectorback.exchange.response.user.BasicUserResponse;
import org.ahv.passwordprotectorback.exchange.response.user.UserResponse;
import org.ahv.passwordprotectorback.model.User;
import org.ahv.passwordprotectorback.service.ElementService;
import org.ahv.passwordprotectorback.service.PasswordService;
import org.ahv.passwordprotectorback.service.TypeService;
import org.ahv.passwordprotectorback.service.UserService;
import org.ahv.passwordprotectorback.validator.NameValidator;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController extends GlobalController<User> {
    private final UserService userService;
    private final ElementService elementService;
    private final PasswordService passwordService;
    private final TypeService typeService;
    private final ControllerAdapter adapter;
    private final NameValidator nameValidator;

    public UserController(ElementService elementService,
                          PasswordService passwordService,
                          TypeService typeService,
                          UserService userService
    ) {
        this.userService = userService;
        this.elementService = elementService;
        this.passwordService = passwordService;
        this.typeService = typeService;
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
    public UserResponse getUserByEmail(@PathVariable String email) {
        return adapter.convertToUserResponse(userService.findByEmail(email));
    }

    @PostMapping("/user/login")
    @ResponseStatus(HttpStatus.OK)
    public BasicUserResponse login(@Valid @RequestBody UserConnectRequest userRequest) {
        User user = userService.findByUsername(userRequest.getUsername());

        //TODO : Revoir ça avec la mise en place de la sécurité
        if (user != null && user.getPassword().equals(userRequest.getPassword())) {
            return adapter.convertToBasicUserResponse(user);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found");
        }
    }

    @PostMapping("/user")
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