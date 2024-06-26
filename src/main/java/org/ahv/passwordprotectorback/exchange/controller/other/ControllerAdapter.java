package org.ahv.passwordprotectorback.exchange.controller.other;

import lombok.RequiredArgsConstructor;
import org.ahv.passwordprotectorback.exchange.request.element.ElementRequest;
import org.ahv.passwordprotectorback.exchange.request.password.PasswordRequest;
import org.ahv.passwordprotectorback.exchange.request.type.TypeRequest;
import org.ahv.passwordprotectorback.exchange.request.user.UserRequest;
import org.ahv.passwordprotectorback.exchange.response.element.BasicElementResponse;
import org.ahv.passwordprotectorback.exchange.response.element.ElementResponse;
import org.ahv.passwordprotectorback.exchange.response.password.BasicPasswordResponse;
import org.ahv.passwordprotectorback.exchange.response.password.OnlyPasswordResponse;
import org.ahv.passwordprotectorback.exchange.response.password.PasswordResponse;
import org.ahv.passwordprotectorback.exchange.response.type.BasicTypeResponse;
import org.ahv.passwordprotectorback.exchange.response.type.TypeResponse;
import org.ahv.passwordprotectorback.exchange.response.user.BasicUserResponse;
import org.ahv.passwordprotectorback.exchange.response.user.UserResponse;
import org.ahv.passwordprotectorback.model.*;
import org.ahv.passwordprotectorback.service.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;

@RequiredArgsConstructor
public class ControllerAdapter {
    private final ElementService elementService;
    private final PasswordService passwordService;
    private final TypeService typeService;
    private final UserService userService;

    //Elements
    public BasicElementResponse convertToBasicElementResponse(Element element) {
        BasicElementResponse elementResponse = emptyBasicElementResponse();

        if (element != null) {
            elementResponse = BasicElementResponse.builder()
                    .id(element.getId())
                    .name(element.getName())
                    .url(element.getUrl())
                    .passwordCount(element.getPasswordCount())
                    .build();
        }

        return elementResponse;
    }

    public ElementResponse convertToElementResponse(Element element) {
        ElementResponse elementResponse = emptyElementResponse();

        if (element != null) {
            elementResponse = ElementResponse.builder()
                    .id(element.getId())
                    .name(element.getName())
                    .url(element.getUrl())
                    .description(element.getDescription())
                    .passwordCount(element.getPasswordCount())
                    .passwords(
                            passwordService.findAllByElementId(element.getId())
                                    .stream()
                                    .map(this::convertToBasicPasswordResponse).toList()
                    )
                    .type(
                            element.getTypeID() == null ? null :
                                    convertToBasicTypeResponse(typeService.findObjectByID(element.getTypeID()))
                    )
                    .user(
                            convertToBasicUserResponse(userService.findObjectByID(element.getUserID()))
                    )
                    .creationDate(element.getCreationDate())
                    .modificationDate(element.getModificationDate())
                    .build();
        }

        return elementResponse;
    }


    //Passwords
    public BasicPasswordResponse convertToBasicPasswordResponse(Password password) {
        BasicPasswordResponse passwordResponse = emptyBasicPasswordResponse();

        if (password != null) {
            passwordResponse = BasicPasswordResponse.builder()
                    .id(password.getId())
                    .identifier(password.getIdentifier())
                    .comment(password.getComment())
                    .build();
        }

        return passwordResponse;
    }

    public OnlyPasswordResponse convertToOnlyPasswordResponse(String decryptPassword) {
        return OnlyPasswordResponse.builder()
                .password(decryptPassword)
                .build();
    }

    public PasswordResponse convertToPasswordResponse(Password password) {
        PasswordResponse passwordResponse = emptyPasswordResponse();

        if (password != null) {
            passwordResponse = PasswordResponse.builder()
                    .id(password.getId())
                    .identifier(password.getIdentifier())
                    .comment(password.getComment())
                    .element(
                            convertToBasicElementResponse(elementService.findObjectByID(password.getElementID()))
                    )
                    .creationDate(password.getCreationDate())
                    .modificationDate(password.getModificationDate())
                    .build();
        }

        return passwordResponse;
    }


    //Type
    public BasicTypeResponse convertToBasicTypeResponse(Type type) {
        BasicTypeResponse typeResponse = emptyBasicTypeResponse();

        if (type != null) {
            typeResponse = BasicTypeResponse.builder()
                    .id(type.getId())
                    .name(type.getName())
                    .build();
        }

        return typeResponse;
    }

    public TypeResponse convertToTypeResponse(Type type) {
        TypeResponse typeResponse = emptyTypeResponse();

        if (type != null) {
            typeResponse = TypeResponse.builder()
                    .id(type.getId())
                    .name(type.getName())
                    .user(
                            type.getUserID() == null ? null :
                                    convertToBasicUserResponse(userService.findObjectByID(type.getUserID()))
                    )
                    .creationDate(type.getCreationDate())
                    .modificationDate(type.getModificationDate())
                    .build();
        }

        return typeResponse;
    }


    //User
    public BasicUserResponse convertToBasicUserResponse(User user) {
        BasicUserResponse userResponse = emptyBasicUserResponse();

        if (user != null) {
            userResponse = BasicUserResponse.builder()
                    .username(user.getUsername())
                    .elementCount(user.getElementCount())
                    .build();
        }

        return userResponse;
    }

    public UserResponse convertToUserResponse(User user) {
        UserResponse userResponse = emptyUserResponse();

        if (user != null) {
            userResponse = UserResponse.builder()
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .elements(
                            elementService.findAllByUserID(user.getId())
                                    .stream()
                                    .map(this::convertToBasicElementResponse).toList()
                    )
                    .elementCount(user.getElementCount())
                    .creationDate(user.getCreationDate())
                    .modificationDate(user.getModificationDate())
                    .build();
        }

        return userResponse;
    }


    //Convert to Object
    public Element convertToElement(ElementRequest elementRequest) {
        Element element = null;

        if (elementRequest != null) {
            User user = userService.findByUsername(elementRequest.getUsername());

            if (user != null) {
                element = Element.builder()
                        .name(elementRequest.getName())
                        .url(elementRequest.getUrl())
                        .description(elementRequest.getDescription())
                        .typeID(elementRequest.getTypeID())
                        .userID(user.getId())
                        .passwordCount(0)
                        .build();

                element.setCreationDate(LocalDate.now());

            }
        }

        return element;
    }


    public Password convertToPassword(PasswordRequest passwordRequest) {
        Password password = null;

        if (passwordRequest != null) {
            password = Password.builder()
                    .identifier(passwordRequest.getIdentifier())
                    .comment(passwordRequest.getComment())
                    .elementID(passwordRequest.getElementID())
                    .build();

            password.setCreationDate(LocalDate.now());
        }

        return password;
    }

    public Type convertToType(TypeRequest typeRequest) {
        Type type = null;

        if (typeRequest != null) {
            User user = userService.findByUsername(typeRequest.getUsername());

            if (user != null) {
                type = Type.builder()
                        .name(typeRequest.getName())
                        .userID(user.getId())
                        .build();

                type.setCreationDate(LocalDate.now());
            }
        }

        return type;
    }

    public User convertToUser(UserRequest userRequest) {
        User user = null;
        BCryptPasswordEncoder bCrypt = new BCryptPasswordEncoder();

        if (userRequest != null) {
            user = User.builder()
                    .firstName(userRequest.getFirstName())
                    .lastName(userRequest.getLastName())
                    .username(userRequest.getUsername())
                    .email(userRequest.getEmail())
                    .password(bCrypt.encode(userRequest.getPassword()))
                    .elementCount(0)
                    .build();

            user.setCreationDate(LocalDate.now());
        }

        return user;
    }


    //Empty
    private PasswordResponse emptyPasswordResponse() {
        return PasswordResponse.builder()
                .id("")
                .identifier("")
                .comment("")
                .element(emptyBasicElementResponse())
                .creationDate(LocalDate.now())
                .modificationDate(LocalDate.now())
                .build();
    }

    private TypeResponse emptyTypeResponse() {
        return TypeResponse.builder()
                .id("")
                .name("")
                .user(emptyBasicUserResponse())
                .creationDate(LocalDate.now())
                .modificationDate(LocalDate.now())
                .build();
    }

    private UserResponse emptyUserResponse() {
        return UserResponse.builder()
                .firstName("")
                .lastName("")
                .username("")
                .email("")
                .elements(new ArrayList<>())
                .elementCount(0)
                .creationDate(LocalDate.now())
                .modificationDate(LocalDate.now())
                .build();
    }

    private ElementResponse emptyElementResponse() {
        return ElementResponse.builder()
                .id("")
                .name("")
                .url("")
                .description("")
                .passwordCount(0)
                .passwords(new ArrayList<>())
                .type(emptyBasicTypeResponse())
                .user(emptyBasicUserResponse())
                .creationDate(LocalDate.now())
                .modificationDate(LocalDate.now())
                .build();
    }

    //Basic
    private BasicElementResponse emptyBasicElementResponse() {
        return BasicElementResponse.builder()
                .id("")
                .name("")
                .url("")
                .passwordCount(0)
                .build();
    }

    private BasicPasswordResponse emptyBasicPasswordResponse() {
        return BasicPasswordResponse.builder()
                .id("")
                .identifier("")
                .comment("")
                .build();
    }

    private BasicTypeResponse emptyBasicTypeResponse() {
        return BasicTypeResponse.builder()
                .id("")
                .name("")
                .build();
    }

    private BasicUserResponse emptyBasicUserResponse() {
        return BasicUserResponse.builder()
                .username("")
                .elementCount(0)
                .build();
    }
}
