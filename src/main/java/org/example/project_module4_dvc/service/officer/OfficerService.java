package org.example.project_module4_dvc.service.officer;

import org.example.project_module4_dvc.dto.dossier.NewDossierDTO;
import org.example.project_module4_dvc.dto.dossier.ResultDossierDTO;
import org.example.project_module4_dvc.dto.specialist.SpecialistAvailableDTO;
import org.example.project_module4_dvc.dto.websocket.DossierUpdateMessage;
import org.example.project_module4_dvc.entity.ops.OpsDossier;
import org.example.project_module4_dvc.entity.ops.OpsDossierFile;
import org.example.project_module4_dvc.entity.sys.SysUser;
import org.example.project_module4_dvc.mapper.OpsDossierMapper;
import org.example.project_module4_dvc.mapper.OpsDossierResultMapper;
import org.example.project_module4_dvc.repository.ops.OpsDossierFileRepository;
import org.example.project_module4_dvc.repository.ops.OpsDossierRepository;
import org.example.project_module4_dvc.repository.cat.CatWorkflowStepRepository;
import org.example.project_module4_dvc.repository.ops.OpsDossierLogRepository;
import org.example.project_module4_dvc.repository.ops.OpsLogWorkflowStepRepository;
import org.example.project_module4_dvc.repository.ops.OpsDossierResultRepository;
import org.example.project_module4_dvc.repository.sys.SysUserRepository;
import org.example.project_module4_dvc.service.websocket.IWebsocketService;
import org.example.project_module4_dvc.entity.ops.OpsDossierLog;
import org.example.project_module4_dvc.entity.ops.OpsLogWorkflowStep;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OfficerService implements IOfficerService {
    private final OpsDossierMapper opsDossierMapper;
    private final OpsDossierResultMapper opsDossierResultMapper;
    private final OpsDossierRepository opsDossierRepository;
    private final OpsDossierFileRepository opsDossierFileRepository;

    private final OpsDossierResultRepository opsDossierResultRepository;
    private final SysUserRepository sysUserRepository;
    private final OpsDossierLogRepository opsDossierLogRepository;
    private final OpsLogWorkflowStepRepository opsLogWorkflowStepRepository;
    private final CatWorkflowStepRepository catWorkflowStepRepository;

    private final IWebsocketService websocketService;

    public OfficerService(OpsDossierMapper opsDossierMapper, OpsDossierResultMapper opsDossierResultMapper,
            OpsDossierRepository opsDossierRepository, OpsDossierFileRepository opsDossierFileRepository,
            OpsDossierResultRepository opsDossierResultRepository, SysUserRepository sysUserRepository,
            OpsDossierLogRepository opsDossierLogRepository,
            OpsLogWorkflowStepRepository opsLogWorkflowStepRepository,
            CatWorkflowStepRepository catWorkflowStepRepository,
            IWebsocketService websocketService) {
        this.opsDossierMapper = opsDossierMapper;
        this.opsDossierResultMapper = opsDossierResultMapper;
        this.opsDossierRepository = opsDossierRepository;
        this.opsDossierFileRepository = opsDossierFileRepository;
        this.opsDossierResultRepository = opsDossierResultRepository;
        this.sysUserRepository = sysUserRepository;
        this.opsDossierLogRepository = opsDossierLogRepository;
        this.opsLogWorkflowStepRepository = opsLogWorkflowStepRepository;
        this.catWorkflowStepRepository = catWorkflowStepRepository;
        this.websocketService = websocketService;
    }

    @Override
    public List<SpecialistAvailableDTO> getAvailableSpecialists(Long dossierId) {
        return sysUserRepository.findAvailableSpecialistsForDossier(dossierId);
    }

    // ho so vua tao
    @Override
    public Page<NewDossierDTO> findAll(String dossierStatus, String departmentName, Pageable pageable) {
        return opsDossierRepository
                .findOpsDossierByDossierStatusAndReceivingDept_DeptName(dossierStatus, departmentName, pageable)
                .map(opsDossierMapper::toDTO);
    }

    // kq tra ve
    @Override
    public Page<ResultDossierDTO> findAllResult(String departmentName, Pageable pageable) {
        return opsDossierResultRepository.findOpsDossierResultByDossier_ReceivingDept_DeptName(
                departmentName, pageable).map(opsDossierResultMapper::toDTO);
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
        return opsDossierRepository.findNearlyDue(now, limit, departmentName).stream().map(opsDossierMapper::toDTO)
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

            if ("PENDING".equals(status)) {
                recordStepCompletion(saved, specialistId, "ACCEPTED", oldStatus, "PENDING", "Hồ sơ đã được tiếp nhận");
            }

            // Real-time updates
            websocketService.broadcastDossierUpdate(saved.getReceivingDept().getDeptName(), saved);
            websocketService.notifyStatusChange(saved.getApplicant().getUsername(), DossierUpdateMessage.builder()
                    .dossierId(saved.getId())
                    .dossierCode(saved.getDossierCode())
                    .oldStatus(oldStatus)
                    .newStatus(status)
                    .handlerName(saved.getCurrentHandler() != null ? saved.getCurrentHandler().getFullName() : null)
                    .build());
        }
    }

    @Override
    @Transactional
    public void updateDossierRejectStatus(Long dossierId, String status, String reason) {
        OpsDossier opsDossier = opsDossierRepository.findById(dossierId).orElse(null);
        if (opsDossier != null) {
            String oldStatus = opsDossier.getDossierStatus();
            opsDossier.setDossierStatus(status);
            opsDossier.setRejectionReason(reason);
            OpsDossier saved = opsDossierRepository.save(opsDossier);

            // Real-time updates
            websocketService.broadcastDossierRemoval(saved.getReceivingDept().getDeptName(), saved.getId(),
                    saved.getDossierCode());
            websocketService.notifyStatusChange(saved.getApplicant().getUsername(), DossierUpdateMessage.builder()
                    .dossierId(saved.getId())
                    .dossierCode(saved.getDossierCode())
                    .oldStatus(oldStatus)
                    .newStatus(status)
                    .comment(reason)
                    .build());
        }
    }

    // websocket
    @Override
    @Transactional
    public void assignDossierToSpecialist(Long dossierId, Long specialistId) {
        OpsDossier dossier = opsDossierRepository.findById(dossierId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ"));

        SysUser specialist = sysUserRepository.findById(specialistId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chuyên viên"));

        String oldStatus = dossier.getDossierStatus();
        String departmentName = dossier.getReceivingDept().getDeptName();

        // Update entity
        dossier.setCurrentHandler(specialist);
        dossier.setDossierStatus("PENDING");
        OpsDossier saved = opsDossierRepository.save(dossier);

        // 🔥 Real-time updates
        // 1. Update officer's list
        websocketService.broadcastDossierUpdate(departmentName, saved);

        // 2. Add to specialist's list
        websocketService.sendToSpecialistList(
                specialist.getUsername(),
                saved,
                "ADD");

        // 3. Send notifications
        websocketService.notifyAssignment(
                specialist.getUsername(),
                saved.getId(),
                saved.getDossierCode(),
                saved.getService().getServiceName());

        websocketService.notifyStatusChange(
                saved.getApplicant().getUsername(),
                DossierUpdateMessage.builder()
                        .dossierId(saved.getId())
                        .dossierCode(saved.getDossierCode())
                        .oldStatus(oldStatus)
                        .newStatus("PENDING")
                        .handlerName(specialist.getFullName())
                        .action("ASSIGNED")
                        .build());

        // 4. Record Workflow Step Completion (Step 1: Tiếp nhận hồ sơ)
        recordStepCompletion(saved, specialistId, "ASSIGNED", oldStatus, "PENDING", "Đã tiếp nhận và phân công xử lý");
    }

    private void recordStepCompletion(OpsDossier dossier, Long actorId, String action, String prevStatus,
            String nextStatus, String comments) {
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

        // Find Step 1 for this service
        catWorkflowStepRepository.findAll().stream()
                .filter(s -> s.getService().getId().equals(dossier.getService().getId()) && s.getStepOrder() == 1)
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
