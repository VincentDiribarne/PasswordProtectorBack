package org.ahv.passwordprotectorback.service;

import java.util.List;

public interface GlobalService<T> {
    List<T> findAll();

    T findObjectByID(String id);

    void save(T object);

    void delete(T object);
}
