package org.ahv.passwordprotectorback.exchange.controller;

import jakarta.validation.Valid;
import org.ahv.passwordprotectorback.exchange.controller.other.ControllerAdapter;
import org.ahv.passwordprotectorback.exchange.controller.other.GlobalController;
import org.ahv.passwordprotectorback.exchange.request.password.PasswordRequest;
import org.ahv.passwordprotectorback.exchange.response.BasicResponse;
import org.ahv.passwordprotectorback.exchange.response.password.BasicPasswordResponse;
import org.ahv.passwordprotectorback.exchange.response.password.OnlyPasswordResponse;
import org.ahv.passwordprotectorback.exchange.response.password.PasswordResponse;
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
    private final ControllerAdapter adapter;
    private final NameValidator nameValidator;

    public PasswordController(ElementService elementService,
                              PasswordService passwordService,
                              TypeService typeService,
                              UserService userService
    ) {
        this.passwordService = passwordService;
        this.adapter = new ControllerAdapter(elementService, passwordService, typeService, userService);
        this.nameValidator = NameValidator.getInstance();
    }


    @GetMapping("/passwords")
    @ResponseStatus(HttpStatus.OK)
    public List<BasicPasswordResponse> getPasswords() {
        return passwordService.findAll().stream().map(adapter::convertToBasicPasswordResponse).toList();
    }


    @GetMapping("/passwords/id/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PasswordResponse getPasswordsByID(@PathVariable String id) {
        return adapter.convertToPasswordResponse(passwordService.findObjectByID(id));
    }


    @GetMapping("/passwords/{elementID}/name/{identifier}")
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


    @PostMapping("/element")
    @ResponseStatus(HttpStatus.CREATED)
    public BasicResponse saveElement(@Valid @RequestBody PasswordRequest passwordRequest) {
        verification(passwordRequest, null);

        //TODO: Encrypt password
        return save(passwordService, adapter.convertToPassword(passwordRequest));
    }


    @PutMapping("/element/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public BasicResponse updateElement(@PathVariable String id, @Valid @RequestBody PasswordRequest passwordRequest) {
        return update(passwordRequest, id);
    }


    @DeleteMapping("/element/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BasicResponse deleteElement(@PathVariable String id) {
        try {
            return delete(passwordService, id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error deleting element", e);
        }
    }


    private BasicResponse update(PasswordRequest passwordRequest, String id) {
        Password passwordToUpdate = passwordService.findObjectByID(id);

        if (passwordRequest != null && passwordToUpdate != null) {
            verification(passwordRequest, passwordToUpdate.getIdentifier());

            passwordToUpdate.setIdentifier(getStringNotNull(passwordRequest.getIdentifier(), passwordToUpdate.getIdentifier()));
            passwordToUpdate.setPassword(getStringNotNull(passwordRequest.getPassword(), passwordToUpdate.getPassword()));
            passwordToUpdate.setComment(getStringNotNull(passwordRequest.getComment(), passwordToUpdate.getComment()));
            passwordToUpdate.setModificationDate(LocalDate.now());

            passwordService.save(passwordToUpdate);

            return BasicResponse.builder().message("Element updated").build();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Element not found");
        }
    }

    private void verification(PasswordRequest passwordRequest, String oldIdentifier) {
        if (nameValidator.isNotValid(passwordService.findAllIdentifier(), oldIdentifier, passwordRequest.getIdentifier())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Identifier already exists");
        }
    }
}