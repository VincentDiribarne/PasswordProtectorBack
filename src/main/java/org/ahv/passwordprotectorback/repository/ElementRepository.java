package org.ahv.passwordprotectorback.repository;

import org.ahv.passwordprotectorback.model.Element;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ElementRepository extends MongoRepository<Element, String> {
    List<Element> findAllByNameContainingIgnoreCase(String name);

    List<Element> findAllByUrlContainingIgnoreCase(String url);

    Element findByUrl(String url);

    Element findByName(String name);
}