package org.ahv.passwordprotectorback.service.impl;

import lombok.RequiredArgsConstructor;
import org.ahv.passwordprotectorback.model.Element;
import org.ahv.passwordprotectorback.repository.ElementRepository;
import org.ahv.passwordprotectorback.service.ElementService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ElementServiceImpl implements ElementService {
    private final ElementRepository elementRepository;

    //Global method
    @Override
    public Element findObjectByID(String id) {
        return elementRepository.findById(id).orElse(null);
    }

    @Override
    public void save(Element element) {
        elementRepository.save(element);
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
}