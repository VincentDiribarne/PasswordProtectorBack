package org.ahv.passwordprotectorback.exchange.controller;

import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ahv.passwordprotectorback.exchange.controller.other.ControllerAdapter;
import org.ahv.passwordprotectorback.exchange.controller.other.GlobalController;
import org.ahv.passwordprotectorback.exchange.request.password.PasswordRequest;
import org.ahv.passwordprotectorback.exchange.request.password.PasswordUpdateRequest;
import org.ahv.passwordprotectorback.exchange.request.password.SharePasswordRequest;
import org.ahv.passwordprotectorback.exchange.response.BasicResponse;
import org.ahv.passwordprotectorback.exchange.response.password.BasicPasswordResponse;
import org.ahv.passwordprotectorback.exchange.response.password.OnlyPasswordResponse;
import org.ahv.passwordprotectorback.exchange.response.password.PasswordResponse;
import org.ahv.passwordprotectorback.model.Element;
import org.ahv.passwordprotectorback.model.Password;
import org.ahv.passwordprotectorback.model.User;
import org.ahv.passwordprotectorback.service.*;
import org.ahv.passwordprotectorback.validator.NameValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/")
@RequiredArgsConstructor
public class PasswordController extends GlobalController<Password> {
    private final UserService userService;
    private final ElementService elementService;
    private final PasswordService passwordService;
    private final TypeService typeService;
    private final EmailService emailService;

    private ControllerAdapter adapter;
    private NameValidator nameValidator;

    @PostConstruct
    public void init() {
        this.adapter = new ControllerAdapter(elementService, passwordService, typeService, userService);
        this.nameValidator = NameValidator.getInstance();
    }


    @GetMapping("/passwords")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<BasicPasswordResponse>> getPasswords() {
        return getResponse(passwordService.findAll().stream().map(adapter::convertToBasicPasswordResponse).toList());
    }


    @GetMapping("/password/id/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PasswordResponse> getPasswordsByID(@PathVariable String id) {
        return getResponse(adapter.convertToPasswordResponse(passwordService.findObjectByID(id)));
    }


    @GetMapping("/password/{elementID}/name/{identifier}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PasswordResponse> getPasswordsByIdentifier(@PathVariable String identifier, @PathVariable String elementID) {
        return getResponse(adapter.convertToPasswordResponse(passwordService.findByElementAndIdentifier(elementID, identifier)));
    }


    @GetMapping("/showPassword/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<OnlyPasswordResponse> showPassword(@PathVariable String id) {
        //TODO : Decrypt password
        return getResponse(adapter.convertToOnlyPasswordResponse(passwordService.findObjectByID(id).getPassword()));
    }


    @PostMapping("/password")
    @ResponseStatus(HttpStatus.CREATED)
    public BasicResponse savePassword(@Valid @RequestBody PasswordRequest passwordRequest) {
        verification(passwordRequest, null, passwordRequest.getElementID());

        //TODO: Encrypt password
        Element element = elementService.findObjectByID(passwordRequest.getElementID());
        if (element != null) {
            element.setPasswordCount(element.getPasswordCount() + 1);
            elementService.save(element);

            return save(passwordService, adapter.convertToPassword(passwordRequest));
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Element not found");
        }
    }


    @PutMapping("/password/elementID/{elementId}/id/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BasicResponse updatePassword(@PathVariable String elementId, @PathVariable String id, @Valid @RequestBody PasswordUpdateRequest passwordRequest) {
        return updatePassword(passwordRequest, elementId, id);
    }


    //Généralisation avec des FunctionalInterfaces
    @DeleteMapping("/password/elementId/{elementId}/id/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BasicResponse deletePassword(@PathVariable String elementId, @PathVariable String id) {
        Element element = elementService.findObjectByID(elementId);

        if (element != null) {
            int newPasswordCount = element.getPasswordCount() - 1;

            if (newPasswordCount == 0) {
                elementService.delete(element);
            } else {
                element.setPasswordCount(newPasswordCount);
                elementService.save(element);
            }

            return delete(passwordService, id);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Element not found");
        }
    }

    @PostMapping("/password/share")
    @ResponseStatus(HttpStatus.OK)
    public BasicResponse sharePassword(@Valid @RequestBody SharePasswordRequest sharePasswordRequest) {
        if (sharePasswordRequest.getEmail() == null) {
            sharePasswordRequest.setEmail("default@email.com");
        }

        Password password = passwordService.findObjectByID(sharePasswordRequest.getPasswordId());
        User user = userService.findByUsername(sharePasswordRequest.getUsername());

        if (password != null && user != null) {
            emailService.sendSharePasswordEmail(sharePasswordRequest.getEmail(), user.getId());

            return BasicResponse.builder().message("Password shared successfully").build();
        } else {
            return BasicResponse.builder().message("Password or user not found").build();
        }
    }


    private BasicResponse updatePassword(PasswordUpdateRequest passwordRequest, String elementId, String id) {
        return update(passwordService, id, passwordToUpdate -> {
            verification(passwordRequest, elementId, passwordToUpdate.getIdentifier());

            passwordToUpdate.setIdentifier(getStringNotNull(passwordToUpdate.getIdentifier(), passwordRequest.getIdentifier()));
            passwordToUpdate.setPassword(getStringNotNull(passwordToUpdate.getPassword(), passwordRequest.getPassword()));
            passwordToUpdate.setComment(getStringNotNull(passwordToUpdate.getComment(), passwordRequest.getComment()));
        });
    }

    //Generalisation ?
    private void verification(PasswordUpdateRequest passwordRequest, String elementID, String oldIdentifier) {
        if (nameValidator.isNotValid(passwordService.findAllIdentifierByElementID(elementID), oldIdentifier, passwordRequest.getIdentifier())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Identifier already exists");
        }
    }
}