package org.ahv.passwordprotectorback.exchange.controller;

import jakarta.validation.Valid;
import org.ahv.passwordprotectorback.exchange.controller.other.ControllerAdapter;
import org.ahv.passwordprotectorback.exchange.controller.other.GlobalController;
import org.ahv.passwordprotectorback.exchange.request.type.TypeRequest;
import org.ahv.passwordprotectorback.exchange.response.BasicResponse;
import org.ahv.passwordprotectorback.exchange.response.type.TypeResponse;
import org.ahv.passwordprotectorback.model.Type;
import org.ahv.passwordprotectorback.service.ElementService;
import org.ahv.passwordprotectorback.service.PasswordService;
import org.ahv.passwordprotectorback.service.TypeService;
import org.ahv.passwordprotectorback.service.UserService;
import org.ahv.passwordprotectorback.validator.NameValidator;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/")
public class TypeController extends GlobalController<Type> {
    private final TypeService typeService;
    private final ControllerAdapter adapter;
    private final NameValidator nameValidator;
    private final UserService userService;

    public TypeController(ElementService elementService,
                          PasswordService passwordService,
                          TypeService typeService,
                          UserService userService
    ) {
        this.typeService = typeService;
        this.userService = userService;
        this.adapter = new ControllerAdapter(elementService, passwordService, typeService, userService);
        this.nameValidator = NameValidator.getInstance();
    }

    @GetMapping("/types")
    @ResponseStatus(HttpStatus.OK)
    public List<TypeResponse> getTypes() {
        return typeService.findAll().stream().map(adapter::convertToTypeResponse).toList();
    }


    @GetMapping("/types/name/{name}")
    @ResponseStatus(HttpStatus.OK)
    public List<TypeResponse> getTypesByName(@PathVariable String name) {
        return typeService.findAllByName(name).stream().map(adapter::convertToTypeResponse).toList();
    }


    @GetMapping("/types/user/{userID}")
    @ResponseStatus(HttpStatus.OK)
    public List<TypeResponse> getTypesByUserID(@PathVariable String userID) {
        return typeService.findAllByUserID(userID).stream().map(adapter::convertToTypeResponse).toList();
    }


    @GetMapping("/type/id/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TypeResponse getTypeByID(@PathVariable String id) {
        return adapter.convertToTypeResponse(typeService.findObjectByID(id));
    }


    @GetMapping("/type/name/{name}")
    @ResponseStatus(HttpStatus.OK)
    public TypeResponse getTypeByName(@PathVariable String name) {
        return adapter.convertToTypeResponse(typeService.findByName(name));
    }


    @PostMapping("/type")
    @ResponseStatus(HttpStatus.CREATED)
    public BasicResponse saveType(@Valid @RequestBody TypeRequest typeRequest) {
        verification(typeRequest, null);

        if (userService.findObjectByID(typeRequest.getUserID()) != null) {
            return save(typeService, adapter.convertToType(typeRequest));
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
    }


    @PutMapping("/type/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BasicResponse updateType(@PathVariable String id, @Valid @RequestBody TypeRequest typeRequest) {
        Type typeToUpdate = typeService.findObjectByID(id);

        if (typeToUpdate != null) {
            verification(typeRequest, typeToUpdate.getName());

            typeToUpdate.setName(getStringNotNull(typeToUpdate.getName(), typeRequest.getName()));
            typeService.save(typeToUpdate);

            return BasicResponse.builder().message("Type updated").build();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Type not found");
        }
    }


    private void verification(TypeRequest typeRequest, String oldName) {
        if (nameValidator.isNotValid(typeService.findAllNamesByUserID(typeRequest.getUserID()), oldName, typeRequest.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This name is already used");
        }
    }
}