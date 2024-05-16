package org.ahv.passwordprotectorback.exchange.controller;

import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ahv.passwordprotectorback.exchange.controller.other.ControllerAdapter;
import org.ahv.passwordprotectorback.exchange.controller.other.GlobalController;
import org.ahv.passwordprotectorback.exchange.request.type.TypeRequest;
import org.ahv.passwordprotectorback.exchange.response.BasicResponse;
import org.ahv.passwordprotectorback.exchange.response.type.BasicTypeResponse;
import org.ahv.passwordprotectorback.exchange.response.type.TypeResponse;
import org.ahv.passwordprotectorback.model.Type;
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
@RequestMapping("/api/")
@RequiredArgsConstructor
public class TypeController extends GlobalController<Type> {
    private final UserService userService;
    private final ElementService elementService;
    private final PasswordService passwordService;
    private final TypeService typeService;

    private NameValidator nameValidator;
    private ControllerAdapter adapter;

    @PostConstruct
    public void init() {
        this.adapter = new ControllerAdapter(elementService, passwordService, typeService, userService);
        this.nameValidator = NameValidator.getInstance();
    }

    @GetMapping("/types")
    @ResponseStatus(HttpStatus.OK)
    public List<BasicTypeResponse> getTypes() {
        return typeService.findAll().stream().map(adapter::convertToBasicTypeResponse).toList();
    }


    @GetMapping("/types/name/{name}")
    @ResponseStatus(HttpStatus.OK)
    public List<BasicTypeResponse> getTypesByName(@PathVariable String name) {
        return typeService.findAllByName(name).stream().map(adapter::convertToBasicTypeResponse).toList();
    }


    @GetMapping("/types/user/{userID}")
    @ResponseStatus(HttpStatus.OK)
    public List<BasicTypeResponse> getTypesByUserID(@PathVariable String userID) {
        return typeService.findAllByUserIDAndNull(userID).stream().map(adapter::convertToBasicTypeResponse).toList();
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

        User user = userService.findByUsername(typeRequest.getUsername());
        if (user != null) {
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
            typeToUpdate.setModificationDate(LocalDate.now());
            typeService.save(typeToUpdate);

            return BasicResponse.builder().message("Type updated").build();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Type not found");
        }
    }

    @DeleteMapping("/type/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BasicResponse deleteType(@PathVariable String id) {
        return delete(typeService, id);
    }


    private void verification(TypeRequest typeRequest, String oldName) {
        User user = userService.findByUsername(typeRequest.getUsername());

        if (nameValidator.isNotValid(typeService.findAllNamesByUserID(user.getId()), oldName, typeRequest.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This name is already used");
        }
    }
}