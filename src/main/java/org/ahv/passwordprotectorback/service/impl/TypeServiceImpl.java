package org.ahv.passwordprotectorback.service.impl;

import lombok.RequiredArgsConstructor;
import org.ahv.passwordprotectorback.model.Type;
import org.ahv.passwordprotectorback.repository.TypeRepository;
import org.ahv.passwordprotectorback.service.TypeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TypeServiceImpl implements TypeService {
    private final TypeRepository typeRepository;

    @Override
    public Type findByName(String name) {
        return typeRepository.findByName(name);
    }

    @Override
    public List<Type> findAllByName(String name) {
        return typeRepository.findAllByNameContainingIgnoreCase(name);
    }

    @Override
    public Type findObjectByID(String id) {
        return typeRepository.findById(id).orElse(null);
    }

    @Override
    public void save(Type type) {
        typeRepository.save(type);
    }
}
