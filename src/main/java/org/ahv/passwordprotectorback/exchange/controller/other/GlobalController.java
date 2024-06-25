package org.ahv.passwordprotectorback.exchange.controller.other;

import org.ahv.passwordprotectorback.exchange.response.BasicResponse;
import org.ahv.passwordprotectorback.model.persistentEntity.PersistentEntity;
import org.ahv.passwordprotectorback.service.GlobalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

public class GlobalController<T extends PersistentEntity> {
    public BasicResponse save(GlobalService<T> service, T object) {
        if (object != null) {
            service.save(object);
            return BasicResponse.builder().message("Saved").build();
        } else {
            return BasicResponse.builder().message("Object is null").build();
        }
    }

    public BasicResponse update(GlobalService<T> service, String id, Updater<T> updater) {
        T objectToUpdate = service.findObjectByID(id);

        if (objectToUpdate != null) {
            updater.update(objectToUpdate);
            objectToUpdate.setModificationDate(LocalDate.now());

            service.save(objectToUpdate);

            return BasicResponse.builder().message("Updated").build();
        } else {
            return BasicResponse.builder().message("Not Found").build();
        }
    }

    public BasicResponse delete(GlobalService<T> service, String id) {
        T object = service.findObjectByID(id);

        if (object != null) {
            service.delete(object);
            return BasicResponse.builder().message("Deleted").build();
        } else {
            return BasicResponse.builder().message("Not Found").build();
        }
    }

    protected String getStringNotNull(String oldString, String newString) {
        return newString == null ? oldString : newString;
    }

    protected <R> ResponseEntity<R> getResponse(R response) {
        return ResponseEntity.ok(response);
    }

    protected <R> ResponseEntity<List<R>> getResponse(List<R> response) {
        if (response != null) {
            return ResponseEntity.ok(response);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not Found");
        }
    }

    @FunctionalInterface
    public interface Updater<T> {
        void update(T objectToUpdate);
    }
}