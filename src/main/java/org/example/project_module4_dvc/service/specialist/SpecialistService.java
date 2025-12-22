package org.example.project_module4_dvc.service.specialist;

import lombok.Setter;
import org.example.project_module4_dvc.dto.dossier.NewDossierDTO;
import org.example.project_module4_dvc.mapper.OpsDossierMapper;
import org.example.project_module4_dvc.repository.ops.OpsDossierRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SpecialistService implements ISpecialistService{
    private final OpsDossierRepository opsDossierRepository;

    private final OpsDossierMapper opsDossierMapper;

    public SpecialistService(OpsDossierRepository opsDossierRepository, OpsDossierMapper opsDossierMapper) {
        this.opsDossierRepository = opsDossierRepository;
        this.opsDossierMapper = opsDossierMapper;
    }

    @Override
    public Page<NewDossierDTO> findAll(String dossierStatus, String departmentName, Long specialistId, Pageable pageable) {
        return opsDossierRepository.findOpsDossierByDossierStatusAndReceivingDept_DeptNameAndCurrentHandler_Id(dossierStatus,departmentName,specialistId,pageable).map(opsDossierMapper::toDTO);
    }

    @Override
    public List<NewDossierDTO> findNearlyDue(String departmentName, Long specialistId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime limit = now.plusHours(6);
        return opsDossierRepository.findNearlyDueSpecialist(now, limit, departmentName,specialistId).stream().map(opsDossierMapper::toDTO)
                .toList();
    }
}
