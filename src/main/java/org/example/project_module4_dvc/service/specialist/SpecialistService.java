package org.example.project_module4_dvc.service.specialist;

import org.example.project_module4_dvc.dto.dossier.NewDossierDTO;
import org.example.project_module4_dvc.entity.ops.OpsDossier;
import org.example.project_module4_dvc.mapper.OpsDossierMapper;
import org.example.project_module4_dvc.repository.cat.CatWorkflowStepRepository;
import org.example.project_module4_dvc.repository.ops.OpsDossierLogRepository;
import org.example.project_module4_dvc.repository.ops.OpsLogWorkflowStepRepository;
import org.example.project_module4_dvc.repository.ops.OpsDossierRepository;
import org.example.project_module4_dvc.repository.sys.SysUserRepository;
import org.example.project_module4_dvc.entity.ops.OpsDossierLog;
import org.example.project_module4_dvc.entity.ops.OpsLogWorkflowStep;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SpecialistService implements ISpecialistService {
    private final OpsDossierRepository opsDossierRepository;

    private final SysUserRepository sysUserRepository;
    private final OpsDossierLogRepository opsDossierLogRepository;
    private final OpsLogWorkflowStepRepository opsLogWorkflowStepRepository;
    private final CatWorkflowStepRepository catWorkflowStepRepository;

    private final OpsDossierMapper opsDossierMapper;

    public SpecialistService(OpsDossierRepository opsDossierRepository, OpsDossierMapper opsDossierMapper,
            SysUserRepository sysUserRepository,
            OpsDossierLogRepository opsDossierLogRepository,
            OpsLogWorkflowStepRepository opsLogWorkflowStepRepository,
            CatWorkflowStepRepository catWorkflowStepRepository) {
        this.opsDossierRepository = opsDossierRepository;
        this.opsDossierMapper = opsDossierMapper;
        this.sysUserRepository = sysUserRepository;
        this.opsDossierLogRepository = opsDossierLogRepository;
        this.opsLogWorkflowStepRepository = opsLogWorkflowStepRepository;
        this.catWorkflowStepRepository = catWorkflowStepRepository;
    }

    @Override
    public Page<NewDossierDTO> findAll(String dossierStatus, String departmentName, Long specialistId,
            Pageable pageable) {
        return opsDossierRepository.findOpsDossierByDossierStatusAndReceivingDept_DeptNameAndCurrentHandler_Id(
                dossierStatus, departmentName, specialistId, pageable).map(opsDossierMapper::toDTO);
    }

    @Override
    public List<NewDossierDTO> findNearlyDue(String departmentName, Long specialistId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime limit = now.plusHours(6);
        return opsDossierRepository.findNearlyDueSpecialist(now, limit, departmentName, specialistId).stream()
                .map(opsDossierMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional
    public void updateDossierStatus(Long dossierId, String status, Long specialistId, LocalDateTime dueDate,
            String reason) {
        OpsDossier opsDossier = opsDossierRepository.findById(dossierId).orElse(null);
        if (opsDossier != null) {
            String oldStatus = opsDossier.getDossierStatus();
            opsDossier.setDossierStatus(status);
            opsDossier.setDueDate(dueDate);
            opsDossier.setCurrentHandler(sysUserRepository.findById(specialistId).orElse(null));
            opsDossier.setRejectionReason(reason);
            OpsDossier saved = opsDossierRepository.save(opsDossier);

            // Ghi log bước 2: Thẩm định hồ sơ
            if ("VERIFIED".equals(status)) {
                String comments = "Đã thẩm định hồ sơ, chờ lãnh đạo phê duyệt";
                recordStepCompletion(saved, specialistId, "THAM_DINH", oldStatus, "VERIFIED", comments, 2);
            }
        }
    }

    private void recordStepCompletion(OpsDossier dossier, Long actorId, String action, String prevStatus,
            String nextStatus, String comments, int stepOrder) {
        // Create Log
        OpsDossierLog log = OpsDossierLog.builder()
                .dossier(dossier)
                .actorId(actorId)
                .action(action)
                .prevStatus(prevStatus)
                .nextStatus(nextStatus)
                .comments(comments)
                .build();
        OpsDossierLog savedLog = opsDossierLogRepository.save(log);

        // Find Step for this service
        catWorkflowStepRepository.findAll().stream()
                .filter(s -> s.getService().getId().equals(dossier.getService().getId())
                        && s.getStepOrder() == stepOrder)
                .findFirst()
                .ifPresent(step -> {
                    OpsLogWorkflowStep lws = new OpsLogWorkflowStep();
                    lws.setLog(savedLog);
                    lws.setWorkflowStep(step);
                    lws.setDescription(step.getStepName() + " hoàn thành");
                    lws.setCreatedAt(java.time.Instant.now());
                    opsLogWorkflowStepRepository.save(lws);
                });
    }
}
