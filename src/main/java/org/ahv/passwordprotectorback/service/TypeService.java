package org.ahv.passwordprotectorback.service;

import org.ahv.passwordprotectorback.model.Type;

import java.util.List;

public interface TypeService extends GlobalService<Type> {
    Type findByName(String name);

    List<Type> findAllByName(String name);

    List<Type> findAllByUserId(String userID);

    List<Type> findAllByUserIDAndNull(String userID);

    List<String> findAllNamesByUserID(String userID);
}
