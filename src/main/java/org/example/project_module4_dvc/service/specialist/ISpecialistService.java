package org.example.project_module4_dvc.service.specialist;

import org.example.project_module4_dvc.dto.dossier.NewDossierDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface ISpecialistService {
    Page<NewDossierDTO> findAll(String dossierStatus, String departmentName, Long specialistId, String paymentStatus,Pageable pageable);

    List<NewDossierDTO> findNearlyDue(String departmentName, Long specialistId);

    void updateDossierStatus(Long dossierId, String status, Long specialistId, LocalDateTime dueDate, String reason);

}
