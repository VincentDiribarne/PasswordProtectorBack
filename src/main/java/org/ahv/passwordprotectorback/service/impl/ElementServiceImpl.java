package org.ahv.passwordprotectorback.service.impl;

import lombok.RequiredArgsConstructor;
import org.ahv.passwordprotectorback.model.Element;
import org.ahv.passwordprotectorback.repository.ElementRepository;
import org.ahv.passwordprotectorback.service.ElementService;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ElementServiceImpl implements ElementService {
    private final ElementRepository elementRepository;

    //Global method
    @Override
    public List<Element> findAll() {
        return elementRepository.findAll().stream().sorted(Comparator.comparing(Element::getName)).toList();
    }

    @Override
    public Element findObjectByID(String id) {
        return elementRepository.findById(id).orElse(null);
    }

    @Override
    public void save(Element element) {
        elementRepository.save(element);
    }

    @Override
    public void delete(Element object) {
        elementRepository.delete(object);
    }


    @Override
    public List<Element> findAllByUserID(String userID) {
        return elementRepository.findAllByUserID(userID);
    }

    //ElementService methods
    @Override
    public List<Element> findAllByName(String name) {
        return elementRepository.findAllByNameContainingIgnoreCase(name);
    }

    @Override
    public List<Element> findAllByURL(String url) {
        return elementRepository.findAllByUrlContainingIgnoreCase(url);
    }

    @Override
    public Element findByURL(String url) {
        return elementRepository.findByUrl(url);
    }

    @Override
    public Element findByName(String name) {
        return elementRepository.findByName(name);
    }


    @Override
    public List<String> findAllName() {
        return findAll().stream().map(Element::getName).toList();
    }

    @Override
    public List<String> findAllURL() {
        return findAll().stream().map(Element::getUrl).toList();
    }
}