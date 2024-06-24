package org.ahv.passwordprotectorback.exchange.controller;

import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ahv.passwordprotectorback.exchange.controller.other.ControllerAdapter;
import org.ahv.passwordprotectorback.exchange.controller.other.GlobalController;
import org.ahv.passwordprotectorback.exchange.request.element.ElementRequest;
import org.ahv.passwordprotectorback.exchange.request.element.ElementUpdateRequest;
import org.ahv.passwordprotectorback.exchange.response.BasicResponse;
import org.ahv.passwordprotectorback.exchange.response.element.BasicElementResponse;
import org.ahv.passwordprotectorback.exchange.response.element.ElementResponse;
import org.ahv.passwordprotectorback.model.Element;
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
@RequiredArgsConstructor
public class ElementController extends GlobalController<Element> {
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


    @GetMapping("/elements")
    @ResponseStatus(HttpStatus.OK)
    public List<BasicElementResponse> getElements() {
        return elementService.findAll().stream().map(adapter::convertToBasicElementResponse).toList();
    }


    @GetMapping("/elements/name/{name}")
    @ResponseStatus(HttpStatus.OK)
    public List<BasicElementResponse> getElementsByName(@PathVariable String name) {
        return elementService.findAllByName(name).stream().map(adapter::convertToBasicElementResponse).toList();
    }

    @GetMapping("/elements/user/{name}")
    @ResponseStatus(HttpStatus.OK)
    public List<Element> getElementsByUserName(@PathVariable String name) {
        return elementService.findAllByUserName(name);
    }

    @GetMapping("/elements/url/{url}")
    @ResponseStatus(HttpStatus.OK)
    public List<BasicElementResponse> getElementsByURL(@PathVariable String url) {
        return elementService.findAllByURL(url).stream().map(adapter::convertToBasicElementResponse).toList();
    }


    @GetMapping("/element/id/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ElementResponse getElement(@PathVariable String id) {
        return adapter.convertToElementResponse(elementService.findObjectByID(id));
    }


    @GetMapping("/element/name/{name}")
    @ResponseStatus(HttpStatus.OK)
    public ElementResponse getElementByName(@PathVariable String name) {
        return adapter.convertToElementResponse(elementService.findByName(name));
    }


    @GetMapping("/element/url/{url}")
    @ResponseStatus(HttpStatus.OK)
    public ElementResponse getElementByURL(@PathVariable String url) {
        return adapter.convertToElementResponse(elementService.findByURL(url));
    }


    @PostMapping("/element")
    @ResponseStatus(HttpStatus.CREATED)
    public BasicResponse saveElement(@Valid @RequestBody ElementRequest elementRequest) {
        verification(elementRequest, null, null);

        User user = userService.findByUsername(elementRequest.getUsername());
        if (user != null) {
            user.setElementCount(user.getElementCount() + 1);
            userService.save(user);

            return save(elementService, adapter.convertToElement(elementRequest));
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
    }


    @PutMapping("/element/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public BasicResponse updateElement(@PathVariable String id, @Valid @RequestBody ElementUpdateRequest element) {
        return update(element, id);
    }


    @DeleteMapping("/element/user/{userId}/id/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BasicResponse deleteElement(@PathVariable String userId, @PathVariable String id) {
        User user = userService.findObjectByID(userId);

        if (user != null) {
            return delete(user, id);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
    }


    //Method
    private BasicResponse update(ElementUpdateRequest elementRequest, String id) {
        Element elementToUpdate = elementService.findObjectByID(id);

        if (elementRequest != null && elementToUpdate != null) {
            verification(elementRequest, elementToUpdate.getName(), elementToUpdate.getUrl());

            elementToUpdate.setName(getStringNotNull(elementToUpdate.getName(), elementRequest.getName()));
            elementToUpdate.setUrl(getStringNotNull(elementToUpdate.getUrl(), elementRequest.getUrl()));
            elementToUpdate.setDescription(getStringNotNull(elementToUpdate.getDescription(), elementRequest.getDescription()));
            elementToUpdate.setTypeID(getStringNotNull(elementToUpdate.getTypeID(), elementRequest.getTypeID()));

            elementToUpdate.setModificationDate(LocalDate.now());
            elementService.save(elementToUpdate);

            return BasicResponse.builder().message("Element updated").build();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Element not found");
        }
    }

    private BasicResponse delete(User user, String id) {
        //User
        user.setElementCount(user.getElementCount() - 1);
        userService.save(user);

        //Password
        passwordService.findAllByElementId(id).forEach(passwordService::delete);

        return delete(elementService, id);
    }

    private void verification(ElementUpdateRequest elementRequest, String oldName, String oldURL) {
        if (nameValidator.isNotValid(elementService.findAllName(), oldName, elementRequest.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Already exist with this name");
        }

        if (nameValidator.isNotValid(elementService.findAllURL(), oldURL, elementRequest.getUrl())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Already exist with this URL");
        }
    }
}