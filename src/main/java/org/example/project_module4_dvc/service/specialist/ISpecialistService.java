package org.example.project_module4_dvc.service.specialist;

import org.example.project_module4_dvc.dto.dossier.NewDossierDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ISpecialistService {
    Page<NewDossierDTO> findAll(String dossierStatus, String departmentName,Long specialistId, Pageable pageable);
}
