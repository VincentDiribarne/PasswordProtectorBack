package org.ahv.passwordprotectorback.exchange.controller;

import jakarta.validation.Valid;
import org.ahv.passwordprotectorback.exchange.controller.other.ControllerAdapter;
import org.ahv.passwordprotectorback.exchange.controller.other.GlobalController;
import org.ahv.passwordprotectorback.exchange.request.element.ElementRequest;
import org.ahv.passwordprotectorback.exchange.request.element.ElementUpdateRequest;
import org.ahv.passwordprotectorback.exchange.response.BasicResponse;
import org.ahv.passwordprotectorback.exchange.response.element.BasicElementResponse;
import org.ahv.passwordprotectorback.exchange.response.element.ElementResponse;
import org.ahv.passwordprotectorback.model.Element;
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
public class ElementController extends GlobalController<Element> {
    private final ElementService elementService;
    private final ControllerAdapter adapter;
    private final NameValidator nameValidator;

    public ElementController(ElementService elementService,
                             PasswordService passwordService,
                             TypeService typeService,
                             UserService userService
    ) {
        this.elementService = elementService;
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

        return save(elementService, adapter.convertToElement(elementRequest));
    }


    @PutMapping("/element/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public BasicResponse updateElement(@PathVariable String id, @Valid @RequestBody ElementUpdateRequest element) {
        return update(element, id);
    }


    @DeleteMapping("/element/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BasicResponse deleteElement(@PathVariable String id) {
        try {
            return delete(elementService, id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error deleting element", e);
        }
    }


    //Method
    private BasicResponse update(ElementUpdateRequest elementRequest, String id) {
        Element elementToUpdate = elementService.findObjectByID(id);

        if (elementRequest != null && elementToUpdate != null) {
            verification(elementRequest, elementToUpdate.getName(), elementToUpdate.getUrl());

            elementToUpdate.setName(elementRequest.getName());
            elementToUpdate.setUrl(elementRequest.getUrl());
            elementToUpdate.setDescription(elementRequest.getDescription());
            elementToUpdate.setTypeID(elementRequest.getTypeID());
            elementToUpdate.setModificationDate(LocalDate.now());
            elementService.save(elementToUpdate);

            return BasicResponse.builder().message("Element updated").build();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Element not found");
        }
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