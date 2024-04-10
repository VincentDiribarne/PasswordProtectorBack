package org.ahv.passwordprotectorback.exchange.controller.other;

import org.ahv.passwordprotectorback.exchange.response.BasicResponse;
import org.ahv.passwordprotectorback.service.GlobalService;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class GlobalController<T> {
    public BasicResponse save(GlobalService<T> service, T object) {
        if (object != null) {
            service.save(object);
            return BasicResponse.builder().message("Object saved").build();
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Object is null");
        }
    }

    public BasicResponse delete(GlobalService<T> service, String id) {
        T object = service.findObjectByID(id);

        if (object != null) {
            service.delete(object);
            return BasicResponse.builder().message("Object deleted").build();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Object not found");
        }
    }

    protected String getStringNotNull(String oldString, String newString) {
        return newString == null ? oldString : newString;
    }
}