package org.ahv.passwordprotectorback.service;

public interface GlobalService<T> {
    T findObjectByID(String id);

    void save(T object);
}
