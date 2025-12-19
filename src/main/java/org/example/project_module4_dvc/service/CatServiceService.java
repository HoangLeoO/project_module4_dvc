package org.example.project_module4_dvc.service;

import org.example.project_module4_dvc.entity.cat.CatService;
import org.example.project_module4_dvc.repository.cat.CatServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CatServiceService implements ICatServiceService{
    @Autowired
    private CatServiceRepository catServiceRepository;

    @Override
    public List<CatService> findAll() {
        return catServiceRepository.findAll();
    }
}
