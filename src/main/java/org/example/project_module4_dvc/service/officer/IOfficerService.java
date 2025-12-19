package org.example.project_module4_dvc.service.officer;

import org.example.project_module4_dvc.dto.dossier.NewDossierDTO;
import org.example.project_module4_dvc.dto.dossier.ResultDossierDTO;
import org.example.project_module4_dvc.entity.ops.OpsDossierFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IOfficerService {
    //lay nhung ho so vua tao tu nguoi dan
    Page<NewDossierDTO> findAll(String dossierStatus,String departmentName, Pageable pageable);

    Page<ResultDossierDTO> findAllResult(String dossierStatus,String departmentName, Pageable pageable);


    NewDossierDTO findById(Long id);

    List<OpsDossierFile> findFileByDossierId(Long dossierId);


    List<NewDossierDTO> findNearlyDue(String departmentName);

    void updateDossierStatus(Long dossierId, String status, String reason);

}
