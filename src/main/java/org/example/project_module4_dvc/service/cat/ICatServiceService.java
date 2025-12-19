package org.example.project_module4_dvc.service.cat;

import org.example.project_module4_dvc.dto.cat.CatServiceDTO;
import java.util.List;

public interface ICatServiceService {
    List<CatServiceDTO> getAllServices();

    List<CatServiceDTO> getFeaturedServices();

    CatServiceDTO getServiceByCode(String code);

    List<CatServiceDTO> searchServices(String keyword, String domain);
}
