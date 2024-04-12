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
import org.ahv.passwordprotectorback.model.Element;
import org.ahv.passwordprotectorback.model.Password;
import org.ahv.passwordprotectorback.model.Type;
import org.ahv.passwordprotectorback.model.User;
import org.ahv.passwordprotectorback.service.ElementService;
import org.ahv.passwordprotectorback.service.PasswordService;
import org.ahv.passwordprotectorback.service.TypeService;
import org.ahv.passwordprotectorback.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;

@RequiredArgsConstructor
public class ControllerAdapter {
    private final ElementService elementService;
    private final PasswordService passwordService;
    private final TypeService typeService;
    private final UserService userService;

    //Elements
    public BasicElementResponse convertToBasicElementResponse(Element element) {
        BasicElementResponse elementResponse = null;

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
        ElementResponse elementResponse = null;

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
        BasicPasswordResponse passwordResponse = null;

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
        PasswordResponse passwordResponse = null;

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
        BasicTypeResponse typeResponse = null;

        if (type != null) {
            typeResponse = BasicTypeResponse.builder()
                    .id(type.getId())
                    .name(type.getName())
                    .build();
        }

        return typeResponse;
    }

    public TypeResponse convertToTypeResponse(Type type) {
        TypeResponse typeResponse = null;

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
        BasicUserResponse userResponse = null;

        if (user != null) {
            userResponse = BasicUserResponse.builder()
                    .username(user.getUsername())
                    .elementCount(user.getElementCount())
                    .build();
        }

        return userResponse;
    }

    public UserResponse convertToUserResponse(User user) {
        UserResponse userResponse = null;

        if (user != null) {
            userResponse = UserResponse.builder()
                    .id(user.getId())
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
            element = Element.builder()
                    .name(elementRequest.getName())
                    .url(elementRequest.getUrl())
                    .description(elementRequest.getDescription())
                    .typeID(elementRequest.getTypeID())
                    .userID(elementRequest.getUserID())
                    .passwordCount(0)
                    .build();

            element.setCreationDate(LocalDate.now());
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
            type = Type.builder()
                    .name(typeRequest.getName())
                    .userID(typeRequest.getUserID())
                    .build();

            type.setCreationDate(LocalDate.now());
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
}
