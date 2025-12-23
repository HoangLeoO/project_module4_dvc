package org.example.project_module4_dvc.service.learder;

import jakarta.transaction.Transactional;
import org.example.project_module4_dvc.dto.leader.DossierApprovalSummaryDTO;
import org.example.project_module4_dvc.entity.ops.OpsDossier;
import org.example.project_module4_dvc.entity.ops.OpsDossierLog;
import org.example.project_module4_dvc.entity.ops.OpsLogWorkflowStep;
import org.example.project_module4_dvc.entity.sys.SysUserDelegation;
import org.example.project_module4_dvc.repository.cat.CatWorkflowStepRepository;
import org.example.project_module4_dvc.repository.leader.LeaderOpsDossierRepository;
import org.example.project_module4_dvc.repository.ops.OpsDossierLogRepository;
import org.example.project_module4_dvc.repository.ops.OpsDossierRepository;
import org.example.project_module4_dvc.repository.ops.OpsLogWorkflowStepRepository;
import org.example.project_module4_dvc.repository.sys.SysDelegationScopeRepository;
import org.example.project_module4_dvc.repository.sys.SysUserDelegationRepository;
import org.example.project_module4_dvc.repository.sys.SysUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class LeaderService implements ILeaderService {

    @Autowired
    private LeaderOpsDossierRepository leaderOpsDossierRepository;

    @Autowired
    private SysUserRepository sysUserRepository;

    @Autowired
    private SysUserDelegationRepository sysUserDelegationRepository;

    @Autowired
    private SysDelegationScopeRepository sysDelegationScopeRepository;

    @Autowired
    private OpsDossierRepository opsDossierRepository;

    @Autowired
    private OpsDossierLogRepository opsDossierLogRepository;

    @Autowired
    private OpsLogWorkflowStepRepository opsLogWorkflowStepRepository;

    @Autowired
    private CatWorkflowStepRepository catWorkflowStepRepository;

    @Override
    public Page<DossierApprovalSummaryDTO> getMyDossiers(Long leaderId, String applicantName, String domain,
            Pageable pageable) {
        return leaderOpsDossierRepository.findMyPendingDossiers(leaderId, applicantName, domain, pageable);
    }

    @Override
    public Page<DossierApprovalSummaryDTO> getDelegatedDossiers(Long leaderId, String applicantName, String domain,
            Pageable pageable) {
        return leaderOpsDossierRepository.findDelegatedPendingDossiers(leaderId, applicantName, domain, pageable);
    }

    @Override
    public Page<DossierApprovalSummaryDTO> findApprovedHistory(Long leaderId, String applicantName, String domain,
            Pageable pageable) {
        return leaderOpsDossierRepository.findApprovedHistory(leaderId, applicantName, domain, pageable);
    }

    @Override
    @Transactional
    public void approvedByLeader(Long userId, Long dossiersId) {
        OpsDossier dossier = opsDossierRepository.findById(dossiersId).orElse(null);
        if (dossier != null) {
            String oldStatus = dossier.getDossierStatus();
            leaderOpsDossierRepository.updateStatusApprovedDossier(userId, dossiersId);

            // Fetch updated dossier to get full info
            OpsDossier saved = opsDossierRepository.findById(dossiersId).orElse(null);

            // Ghi log bước 3: Phê duyệt hồ sơ
            recordStepCompletion(saved, userId, "PHE_DUYET", oldStatus, "APPROVED", "Lãnh đạo đã phê duyệt hồ sơ", 3);
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

    @Override
    public long countByCurrentHandler_IdAndDossierStatus(Long currentHandlerId, String dossierStatus) {
        return leaderOpsDossierRepository.countByCurrentHandler_IdAndDossierStatus(currentHandlerId, dossierStatus);
    }

    @Override
    public long countDelegatedDossiers(Long delegateeId, String status) {
        return leaderOpsDossierRepository.countDelegatedDossiers(delegateeId, status);
    }

    @Override
    public Double getOnTimeRateByDeptId(Long deptId) {
        return leaderOpsDossierRepository.getOnTimeRateByDeptId(deptId);
    }

    @Override
    public long countAllDossiersByDept(Long deptId) {
        return leaderOpsDossierRepository.countAllDossiersByDept(deptId);
    }

    @Override
    public long countOverdueDossiersByDept(Long deptId) {
        return leaderOpsDossierRepository.countOverdueDossiersByDept(deptId);
    }

    @Override
    public Double getAverageSatisfactionScoreByDept(Long deptId) {
        return leaderOpsDossierRepository.getAverageSatisfactionScoreByDept(deptId);
    }

    @Override
    public org.example.project_module4_dvc.dto.leader.DelegationConfigDTO getDelegationConfigData(Long leaderId) {
        // 1. Get potential delegatees (Mock: All users in same dept except self)
        // In real app, check for 'LEADER_VICE' role
        org.example.project_module4_dvc.entity.sys.SysUser leader = sysUserRepository.findById(leaderId).orElseThrow();
        Long deptId = leader.getDepartment().getId();

        java.util.List<String> targetRoles = java.util.List.of("CHU_TICH_UBND", "PHO_CHU_TICH_UBND");

        java.util.List<org.example.project_module4_dvc.entity.sys.SysUser> potentialDelegatees = sysUserRepository
                .findPotentialDelegatees(
                        deptId,
                        leaderId,
                        targetRoles);

        // 2. Get current delegations
        java.util.List<org.example.project_module4_dvc.entity.sys.SysUserDelegation> currentDelegations = sysUserDelegationRepository
                .findByFromUser_IdOrderByStartTimeDesc(leaderId);

        return new org.example.project_module4_dvc.dto.leader.DelegationConfigDTO(potentialDelegatees,
                currentDelegations);
    }

    @Override
    @Transactional
    public void createDelegation(Long leaderId,
            org.example.project_module4_dvc.dto.leader.DelegationRequestDTO request) {
        // 1. Validation
        if (request.getDelegateeId() == null)
            throw new IllegalArgumentException("Chưa chọn người ủy quyền");
        if (request.getFromDate() == null || request.getToDate() == null)
            throw new IllegalArgumentException("Chưa chọn thời gian");

        // 2. Create Delegation
        SysUserDelegation delegation = new org.example.project_module4_dvc.entity.sys.SysUserDelegation();
        delegation.setFromUser(sysUserRepository.findById(leaderId).orElseThrow());
        delegation.setToUser(sysUserRepository.findById(request.getDelegateeId()).orElseThrow());
        delegation.setStartTime(request.getFromDate().atStartOfDay());
        delegation.setEndTime(request.getToDate().atTime(23, 59, 59));
        delegation.setStatus(1);
        delegation.setNotes("Ủy quyền " + (request.isFullWith() ? "Toàn bộ" : "Theo phạm vi"));

        SysUserDelegation savedDelegation = sysUserDelegationRepository.save(delegation);

        // 3. Create Scopes if not full
        if (!request.isFullWith() && request.getSelectedScopes() != null) {
            for (String scopeStr : request.getSelectedScopes()) {
                // scopeStr format: "TYPE:VALUE" e.g. "DOMAIN:Đất đai"
                String[] parts = scopeStr.split(":");
                if (parts.length == 2) {
                    org.example.project_module4_dvc.entity.sys.SysDelegationScope scope = new org.example.project_module4_dvc.entity.sys.SysDelegationScope();
                    scope.setDelegation(savedDelegation);
                    scope.setScopeType(parts[0]);
                    scope.setScopeValue(parts[1]);
                    sysDelegationScopeRepository.save(scope);
                }
            }
        }
    }

    @Override
    public void revokeDelegation(Long delegationId) {
        sysUserDelegationRepository.deleteById(delegationId);
    }
}
