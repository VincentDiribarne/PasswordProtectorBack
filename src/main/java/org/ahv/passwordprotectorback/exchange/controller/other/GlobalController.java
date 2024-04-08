package org.ahv.passwordprotectorback.exchange.controller.other;

import org.ahv.passwordprotectorback.exchange.response.BasicResponse;
import org.ahv.passwordprotectorback.service.GlobalService;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class GlobalController<T> {
    public BasicResponse save(GlobalService<T> service, T element) {
        if (element != null) {
            service.save(element);
            return BasicResponse.builder().message("Element saved").build();
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Element is null");
        }
    }

    public BasicResponse delete(GlobalService<T> service, String id) {
        T element = service.findObjectByID(id);

        if (element != null) {
            service.delete(element);
            return BasicResponse.builder().message("Element deleted").build();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Element not found");
        }
    }

    protected String getStringNotNull(String oldString, String newString) {
        return newString == null ? oldString : newString;
    }
}
