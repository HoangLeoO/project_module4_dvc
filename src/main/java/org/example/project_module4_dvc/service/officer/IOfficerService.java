package org.example.project_module4_dvc.service.officer;

import org.example.project_module4_dvc.dto.dossier.NewDossierDTO;
import org.example.project_module4_dvc.dto.dossier.ResultDossierDTO;
import org.example.project_module4_dvc.dto.specialist.SpecialistAvailableDTO;
import org.example.project_module4_dvc.entity.ops.OpsDossierFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface IOfficerService {

    public List<SpecialistAvailableDTO> getAvailableSpecialists(Long dossierId);

    // lay nhung ho so vua tao tu nguoi dan
    Page<NewDossierDTO> findAll(String dossierStatus, String departmentName, Pageable pageable);

    Page<ResultDossierDTO> findAllResult(String departmentName, Pageable pageable);

    NewDossierDTO findById(Long id);

    List<OpsDossierFile> findFileByDossierId(Long dossierId);

    List<NewDossierDTO> findNearlyDue(String departmentName);

    void updateDossierStatus(Long dossierId, String status, Long specialistId, LocalDateTime dueDate, String reason);

    void updateDossierRejectStatus(Long dossierId, String status, String reason);

    // websocket
    public void assignDossierToSpecialist(Long dossierId, Long specialistId);

}
