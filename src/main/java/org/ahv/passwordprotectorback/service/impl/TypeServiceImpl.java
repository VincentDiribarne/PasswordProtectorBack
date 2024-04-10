package org.ahv.passwordprotectorback.service.impl;

import lombok.RequiredArgsConstructor;
import org.ahv.passwordprotectorback.model.Type;
import org.ahv.passwordprotectorback.repository.TypeRepository;
import org.ahv.passwordprotectorback.service.TypeService;
import org.springframework.stereotype.Service;

import java.util.Comparator;
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
    public List<Type> findAllByUserIDAndNull(String userID) {
        return findAll().stream().filter(type -> type.getUserID() == null || userID.equals(type.getUserID())).toList();
    }

    @Override
    public List<Type> findAllByUserId(String userID) {
        return typeRepository.findAllByUserID(userID);
    }

    @Override
    public List<String> findAllNamesByUserID(String userID) {
        return findAllByUserIDAndNull(userID).stream().map(Type::getName).toList();
    }


    //Global method
    @Override
    public List<Type> findAll() {
        return typeRepository.findAll().stream().sorted(Comparator.comparing(Type::getName)).toList();
    }

    @Override
    public Type findObjectByID(String id) {
        return typeRepository.findById(id).orElse(null);
    }

    @Override
    public void save(Type type) {
        typeRepository.save(type);
    }

    @Override
    public void delete(Type object) {
        typeRepository.delete(object);
    }
}
