package org.ahv.passwordprotectorback.exchange.controller;

import jakarta.validation.Valid;
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
@RequestMapping("/api/")
public class PasswordController extends GlobalController<Password> {
    private final PasswordService passwordService;
    private final ElementService elementService;
    private final ControllerAdapter adapter;
    private final NameValidator nameValidator;

    public PasswordController(ElementService elementService,
                              PasswordService passwordService,
                              TypeService typeService,
                              UserService userService
    ) {
        this.passwordService = passwordService;
        this.elementService = elementService;
        this.adapter = new ControllerAdapter(elementService, passwordService, typeService, userService);
        this.nameValidator = NameValidator.getInstance();
    }


    @GetMapping("/passwords")
    @ResponseStatus(HttpStatus.OK)
    public List<BasicPasswordResponse> getPasswords() {
        return passwordService.findAll().stream().map(adapter::convertToBasicPasswordResponse).toList();
    }


    @GetMapping("/password/id/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PasswordResponse getPasswordsByID(@PathVariable String id) {
        return adapter.convertToPasswordResponse(passwordService.findObjectByID(id));
    }


    @GetMapping("/password/{elementID}/name/{identifier}")
    @ResponseStatus(HttpStatus.OK)
    public PasswordResponse getPasswordsByIdentifier(@PathVariable String identifier, @PathVariable String elementID) {
        return adapter.convertToPasswordResponse(passwordService.findByElementAndIdentifier(elementID, identifier));
    }


    @GetMapping("/showPassword/{id}")
    @ResponseStatus(HttpStatus.OK)
    public OnlyPasswordResponse showPassword(@PathVariable String id) {
        //TODO : Decrypt password
        return adapter.convertToOnlyPasswordResponse(passwordService.findObjectByID(id).getPassword());
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
        return update(passwordRequest, elementId, id);
    }


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


    private BasicResponse update(PasswordUpdateRequest passwordRequest, String elementId, String id) {
        Password passwordToUpdate = passwordService.findObjectByID(id);

        if (passwordRequest != null && passwordToUpdate != null) {
            verification(passwordRequest, elementId, passwordToUpdate.getIdentifier());

            passwordToUpdate.setIdentifier(getStringNotNull(passwordToUpdate.getIdentifier(), passwordRequest.getIdentifier()));
            passwordToUpdate.setPassword(getStringNotNull(passwordToUpdate.getPassword(), passwordRequest.getPassword()));
            passwordToUpdate.setComment(getStringNotNull(passwordToUpdate.getComment(), passwordRequest.getComment()));
            passwordToUpdate.setModificationDate(LocalDate.now());

            passwordService.save(passwordToUpdate);

            return BasicResponse.builder().message("Password updated").build();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Password not found");
        }
    }

    private void verification(PasswordUpdateRequest passwordRequest, String elementID, String oldIdentifier) {
        if (nameValidator.isNotValid(passwordService.findAllIdentifierByElementID(elementID), oldIdentifier, passwordRequest.getIdentifier())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Identifier already exists");
        }
    }
}