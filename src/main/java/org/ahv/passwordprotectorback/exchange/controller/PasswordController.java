package org.ahv.passwordprotectorback.exchange.controller;

import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ahv.passwordprotectorback.exchange.controller.other.ControllerAdapter;
import org.ahv.passwordprotectorback.exchange.controller.other.GlobalController;
import org.ahv.passwordprotectorback.exchange.request.password.PasswordRequest;
import org.ahv.passwordprotectorback.exchange.request.password.PasswordUpdateRequest;
import org.ahv.passwordprotectorback.exchange.response.BasicResponse;
import org.ahv.passwordprotectorback.exchange.response.password.BasicPasswordResponse;
import org.ahv.passwordprotectorback.exchange.response.password.OnlyPasswordResponse;
import org.ahv.passwordprotectorback.exchange.response.password.PasswordResponse;
import org.ahv.passwordprotectorback.model.Element;
import org.ahv.passwordprotectorback.model.Password;
import org.ahv.passwordprotectorback.model.User;
import org.ahv.passwordprotectorback.service.ElementService;
import org.ahv.passwordprotectorback.service.PasswordService;
import org.ahv.passwordprotectorback.service.TypeService;
import org.ahv.passwordprotectorback.service.UserService;
import org.ahv.passwordprotectorback.validator.NameValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.ahv.passwordprotectorback.security.PasswordSecurity.*;

@RestController
@RequestMapping("/api/")
@RequiredArgsConstructor
public class PasswordController extends GlobalController<Password> {
    private final UserService userService;
    private final ElementService elementService;
    private final PasswordService passwordService;
    private final TypeService typeService;

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
    public ResponseEntity<OnlyPasswordResponse> showPassword(@PathVariable String id) throws Exception {
        Password password = passwordService.findObjectByID(id);
        Element element = elementService.findObjectByID(password.getElementID());
        User user = userService.findObjectByID(element.getUserID());

        String clearPassword = decryptPassword(password.getPassword(), generateKey(user.getId()));
        return getResponse(adapter.convertToOnlyPasswordResponse(clearPassword));
    }

    @PostMapping("/password")
    @ResponseStatus(HttpStatus.CREATED)
    public BasicResponse savePassword(@Valid @RequestBody PasswordRequest passwordRequest) throws Exception {
        //verification(passwordRequest, null, passwordRequest.getElementID());
        Element element = elementService.findObjectByID(passwordRequest.getElementID());

        if (element != null) {
            passwordRequest.setPassword(encryptPassword(passwordRequest.getPassword(), generateKey(element.getUserID())));
            element.setPasswordCount(element.getPasswordCount() + 1);
            elementService.save(element);

            return save(passwordService, adapter.convertToPassword(passwordRequest));
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Element not found");
        }
    }


    @PutMapping("/password/id/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BasicResponse updatePassword(@PathVariable String id, @Valid @RequestBody PasswordUpdateRequest passwordRequest) throws Exception {
        String elementID = passwordService.findObjectByID(id).getElementID();
        Element element = elementService.findObjectByID(elementID);

        return update(passwordService, id, passwordToUpdate -> {
            //verification(passwordRequest, elementId, passwordToUpdate.getIdentifier());

            passwordToUpdate.setIdentifier(getStringNotNull(passwordToUpdate.getIdentifier(), passwordRequest.getIdentifier()));

            String password = encryptPassword(passwordRequest.getPassword(), generateKey(element.getUserID()));
            passwordToUpdate.setPassword(getStringNotNull(passwordToUpdate.getPassword(), password));
            passwordToUpdate.setComment(getStringNotNull(passwordToUpdate.getComment(), passwordRequest.getComment()));
        });
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

    //Generalisation ?
    private void verification(PasswordUpdateRequest passwordRequest, String elementID, String oldIdentifier) {
        if (nameValidator.isNotValid(passwordService.findAllIdentifierByElementID(elementID), oldIdentifier, passwordRequest.getIdentifier())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Identifier already exists");
        }
    }
}