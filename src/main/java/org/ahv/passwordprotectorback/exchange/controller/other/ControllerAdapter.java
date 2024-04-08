package org.ahv.passwordprotectorback.exchange.controller.other;

import lombok.RequiredArgsConstructor;
import org.ahv.passwordprotectorback.exchange.request.element.ElementRequest;
import org.ahv.passwordprotectorback.exchange.request.password.PasswordRequest;
import org.ahv.passwordprotectorback.exchange.response.element.BasicElementResponse;
import org.ahv.passwordprotectorback.exchange.response.element.ElementResponse;
import org.ahv.passwordprotectorback.exchange.response.password.BasicPasswordResponse;
import org.ahv.passwordprotectorback.exchange.response.password.OnlyPasswordResponse;
import org.ahv.passwordprotectorback.exchange.response.password.PasswordResponse;
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

import java.time.LocalDate;

@RequiredArgsConstructor
public class ControllerAdapter {
    private final ElementService elementService;
    private final PasswordService passwordService;
    private final TypeService typeService;
    private final UserService userService;


    //Elements
    public BasicElementResponse convertToBasicElementResponse(Element element) {
        return BasicElementResponse.builder()
                .name(element.getName())
                .url(element.getUrl())
                .passwordCount(element.getPasswordCount())
                .build();
    }

    public ElementResponse convertToElementResponse(Element element) {
        return ElementResponse.builder()
                .name(element.getName())
                .url(element.getUrl())
                .passwordCount(element.getPasswordCount())
                .passwords(
                        passwordService.findAllByElementId(element.getId())
                                .stream()
                                .map(this::convertToBasicPasswordResponse).toList()
                )
                .type(
                        convertToTypeResponse(typeService.findObjectByID(element.getTypeID()))
                )
                .user(
                        convertToBasicUserResponse(userService.findObjectByID(element.getUserID()))
                )
                .build();
    }


    //Passwords
    public BasicPasswordResponse convertToBasicPasswordResponse(Password password) {
        return BasicPasswordResponse.builder()
                .identifier(password.getIdentifier())
                .comment(password.getComment())
                .build();
    }

    public OnlyPasswordResponse convertToOnlyPasswordResponse(String decryptPassword) {
        return OnlyPasswordResponse.builder()
                .password(decryptPassword)
                .build();
    }

    public PasswordResponse convertToPasswordResponse(Password password) {
        return PasswordResponse.builder()
                .identifier(password.getIdentifier())
                .comment(password.getComment())
                .element(
                        convertToBasicElementResponse(elementService.findObjectByID(password.getElementID()))
                )
                .build();
    }


    //Type
    public TypeResponse convertToTypeResponse(Type type) {
        return TypeResponse.builder()
                .name(type.getName())
                .build();
    }


    //User
    public BasicUserResponse convertToBasicUserResponse(User user) {
        return BasicUserResponse.builder()
                .username(user.getUsername())
                .elementCount(user.getElementCount())
                .build();
    }

    public UserResponse convertToUserResponse(User user) {
        return UserResponse.builder()
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
                .build();
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
        }

        return password;
    }
}
