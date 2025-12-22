package org.example.project_module4_dvc.service.cat;

import org.example.project_module4_dvc.dto.cat.CatServiceDTO;
import org.example.project_module4_dvc.entity.cat.CatService;

import java.util.List;
import java.util.Optional;

public interface ICatServiceService {
    List<CatServiceDTO> getAllServices();

    List<CatServiceDTO> getFeaturedServices();

    CatServiceDTO getServiceByCode(String code);

    List<CatServiceDTO> searchServices(String keyword, String domain);

    List<CatService> findAll();

    CatService save(CatService service);

    void deleteById(Long id);

    Optional<CatService> findById(Long id);
}
