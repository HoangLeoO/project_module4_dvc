package org.example.project_module4_dvc.service.officer;

import org.example.project_module4_dvc.dto.dossier.NewDossierDTO;
import org.example.project_module4_dvc.dto.dossier.ResultDossierDTO;
import org.example.project_module4_dvc.entity.ops.OpsDossier;
import org.example.project_module4_dvc.entity.ops.OpsDossierFile;
import org.example.project_module4_dvc.mapper.OpsDossierMapper;
import org.example.project_module4_dvc.mapper.OpsDossierResultMapper;
import org.example.project_module4_dvc.repository.ops.OpsDossierFileRepository;
import org.example.project_module4_dvc.repository.ops.OpsDossierRepository;
import org.example.project_module4_dvc.repository.ops.OpsDossierResultRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OfficerService implements IOfficerService {
    private final OpsDossierMapper opsDossierMapper;
    private final OpsDossierResultMapper opsDossierResultMapper;
    private final OpsDossierRepository opsDossierRepository;
    private final OpsDossierFileRepository opsDossierFileRepository;

    private final OpsDossierResultRepository opsDossierResultRepository;

    public OfficerService(OpsDossierMapper opsDossierMapper, OpsDossierResultMapper opsDossierResultMapper, OpsDossierRepository opsDossierRepository, OpsDossierFileRepository opsDossierFileRepository, OpsDossierResultRepository opsDossierResultRepository) {
        this.opsDossierMapper = opsDossierMapper;
        this.opsDossierResultMapper = opsDossierResultMapper;
        this.opsDossierRepository = opsDossierRepository;
        this.opsDossierFileRepository = opsDossierFileRepository;
        this.opsDossierResultRepository = opsDossierResultRepository;
    }

    //ho so vua tao
    @Override
    public Page<NewDossierDTO> findAll(String dossierStatus,String departmentName, Pageable pageable) {
        return opsDossierRepository.findOpsDossierByDossierStatusAndReceivingDept_DeptName(dossierStatus,departmentName, pageable).map(opsDossierMapper::toDTO);
    }

    //kq tra ve
    @Override
    public Page<ResultDossierDTO> findAllResult(String dossierStatus,String departmentName, Pageable pageable) {
        return opsDossierResultRepository.findOpsDossierResultByDossier_DossierStatusAndDossier_ReceivingDept_DeptName(dossierStatus, departmentName,pageable).map(opsDossierResultMapper::toDTO);
    }

    @Override
    public NewDossierDTO findById(Long id) {
        return opsDossierMapper.toDTO(opsDossierRepository.findById(id).orElse(null));
    }

    @Override
    public List<OpsDossierFile> findFileByDossierId(Long dossierId) {
        return opsDossierFileRepository.findOpsDossierFileByDossier_Id(dossierId).stream().toList();
    }

    @Override
    public List<NewDossierDTO> findNearlyDue(String departmentName) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime limit = now.plusHours(6);
        return opsDossierRepository.findNearlyDue(now, limit,departmentName).stream().map(opsDossierMapper::toDTO).toList();
    }

    @Override
    public void updateDossierStatus(Long dossierId, String status, String reason) {
        OpsDossier opsDossier = opsDossierRepository.findById(dossierId).orElse(null);
        if (opsDossier != null) {
            opsDossier.setDossierStatus(status);
            opsDossier.setRejectionReason(reason);
            opsDossierRepository.save(opsDossier);
        }
    }

    //tét

}
