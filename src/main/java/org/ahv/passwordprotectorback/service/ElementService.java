package org.ahv.passwordprotectorback.service;

import org.ahv.passwordprotectorback.model.Element;

import java.util.List;

public interface ElementService extends GlobalService<Element> {
    List<Element> findAllByUserID(String userID);

    List<Element> findAllByName(String name);

    List<Element> findAllByURL(String url);

    List<Element> findAllByUserName(String username);

    Element findByURL(String url);

    Element findByName(String name);

    //Verification
    List<String> findAllName();

    List<String> findAllURL();
}
