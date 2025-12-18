package org.example.project_module4_dvc.service.officer;

import org.example.project_module4_dvc.dto.dossier.NewDossierDTO;
import org.example.project_module4_dvc.entity.ops.OpsDossier;
import org.example.project_module4_dvc.entity.ops.OpsDossierFile;
import org.example.project_module4_dvc.mapper.OpsDossierMapper;
import org.example.project_module4_dvc.repository.ops.OpsDossierFileRepository;
import org.example.project_module4_dvc.repository.ops.OpsDossierRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OfficerService implements IOfficerService {
    private final OpsDossierMapper opsDossierMapper;
    private final OpsDossierRepository opsDossierRepository;
    private final OpsDossierFileRepository opsDossierFileRepository;

    public OfficerService(OpsDossierMapper opsDossierMapper, OpsDossierRepository opsDossierRepository, OpsDossierFileRepository opsDossierFileRepository) {
        this.opsDossierMapper = opsDossierMapper;
        this.opsDossierRepository = opsDossierRepository;
        this.opsDossierFileRepository = opsDossierFileRepository;
    }

    @Override
    public Page<NewDossierDTO> findAll(String dossierStatus, Pageable pageable) {
        return opsDossierRepository.findOpsDossierByDossierStatus(dossierStatus, pageable).map(opsDossierMapper::toDTO);
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
    public List<NewDossierDTO> findNearlyDue() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime limit = now.plusHours(6);
        return opsDossierRepository.findNearlyDue(now, limit).stream().map(opsDossierMapper::toDTO).toList();
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
