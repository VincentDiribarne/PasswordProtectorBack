package org.ahv.passwordprotectorback.service;

import org.ahv.passwordprotectorback.model.Element;

import java.util.List;

public interface ElementService extends GlobalService<Element> {
    List<Element> findAllByName(String name);

    List<Element> findAllByURL(String url);

    Element findByURL(String url);

    Element findByName(String name);
}
