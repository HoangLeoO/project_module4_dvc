package org.example.project_module4_dvc.service.learder;

import jakarta.transaction.Transactional;
import org.example.project_module4_dvc.dto.leader.DelegationConfigDTO;
import org.example.project_module4_dvc.dto.leader.DelegationRequestDTO;
import org.example.project_module4_dvc.dto.leader.DossierApprovalSummaryDTO;
import org.example.project_module4_dvc.dto.leader.report.ReportDomainStatDTO;
import org.example.project_module4_dvc.dto.leader.report.ReportSummaryDTO;
import org.example.project_module4_dvc.entity.ops.OpsDossierResult;
import org.example.project_module4_dvc.entity.sys.SysDelegationScope;
import org.example.project_module4_dvc.entity.sys.SysUser;
import org.example.project_module4_dvc.entity.sys.SysUserDelegation;
import org.example.project_module4_dvc.repository.leader.LeaderOpsDossierRepository;
import org.example.project_module4_dvc.repository.ops.OpsDossierResultRepository;
import org.example.project_module4_dvc.repository.sys.SysDelegationScopeRepository;
import org.example.project_module4_dvc.repository.sys.SysUserDelegationRepository;
import org.example.project_module4_dvc.repository.sys.SysUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LeaderService implements ILeaderService {

    @Autowired
    private LeaderOpsDossierRepository opsDossierRepository;

    @Autowired
    private SysUserRepository sysUserRepository;
    @Autowired
    private SysUserDelegationRepository sysUserDelegationRepository;
    @Autowired
    private SysDelegationScopeRepository sysDelegationScopeRepository;
    @Autowired
    private OpsDossierResultRepository opsDossierResultRepository;
    @Autowired
    private org.example.project_module4_dvc.repository.ops.OpsDossierRepository standardOpsDossierRepository;
    @Autowired
    private org.example.project_module4_dvc.repository.ops.OpsDossierLogRepository dossierLogRepository;
    @Autowired
    private org.example.project_module4_dvc.repository.ops.OpsLogWorkflowStepRepository opsLogWorkflowStepRepository;
    @Autowired
    private org.example.project_module4_dvc.repository.cat.CatWorkflowStepRepository catWorkflowStepRepository;

    @Override
    public Page<DossierApprovalSummaryDTO> getMyDossiers(Long leaderId, String applicantName, String domain,
            Pageable pageable) {
        return opsDossierRepository.findMyPendingDossiers(leaderId, applicantName, domain, pageable);
    }

    @Override
    public Page<DossierApprovalSummaryDTO> getDelegatedDossiers(Long leaderId, String applicantName, String domain,
            Pageable pageable) {
        return opsDossierRepository.findDelegatedPendingDossiers(leaderId, applicantName, domain, pageable);
    }

    @Override
    public Page<DossierApprovalSummaryDTO> findApprovedHistory(Long leaderId, String applicantName, String domain,
            Pageable pageable) {
        return opsDossierRepository.findApprovedHistory(leaderId, applicantName, domain, pageable);
    }

    @Override
    @Transactional
    public void approvedByLeader(Long userId, Long dossierId) {
        // Find dossier
        org.example.project_module4_dvc.entity.ops.OpsDossier dossier = standardOpsDossierRepository.findById(dossierId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ ID: " + dossierId));

        String oldStatus = dossier.getDossierStatus();

        // Update status logic (similar to repository query but in Java)
        dossier.setDossierStatus("APPROVED");
        dossier.setFinishDate(LocalDateTime.now());
        dossier.setCurrentHandler(sysUserRepository.findById(userId).orElse(null));

        org.example.project_module4_dvc.entity.ops.OpsDossier savedDossier = standardOpsDossierRepository.save(dossier);

        // Record Step 3 (Phê duyệt)
        recordStepCompletion(savedDossier, userId, "PHE_DUYET", oldStatus, "APPROVED", "Lãnh đạo đã phê duyệt hồ sơ",
                3);
    }

    private void recordStepCompletion(org.example.project_module4_dvc.entity.ops.OpsDossier dossier, Long actorId,
            String action, String prevStatus,
            String nextStatus, String comments, int stepOrder) {
        // Create Log
        org.example.project_module4_dvc.entity.ops.OpsDossierLog log = new org.example.project_module4_dvc.entity.ops.OpsDossierLog();
        log.setDossier(dossier);
        log.setActorId(actorId);
        log.setAction(action);
        log.setPrevStatus(prevStatus);
        log.setNextStatus(nextStatus);
        log.setComments(comments);
        log.setCreatedAt(LocalDateTime.now());
        org.example.project_module4_dvc.entity.ops.OpsDossierLog savedLog = dossierLogRepository.save(log);

        // Find Step
        catWorkflowStepRepository.findAll().stream()
                .filter(s -> s.getService().getId().equals(dossier.getService().getId())
                        && s.getStepOrder() == stepOrder)
                .findFirst()
                .ifPresent(step -> {
                    org.example.project_module4_dvc.entity.ops.OpsLogWorkflowStep lws = new org.example.project_module4_dvc.entity.ops.OpsLogWorkflowStep();
                    lws.setLog(savedLog);
                    lws.setWorkflowStep(step);
                    lws.setDescription(step.getStepName() + " hoàn thành");
                    lws.setCreatedAt(java.time.Instant.now());
                    opsLogWorkflowStepRepository.save(lws);
                });
    }

    @Override
    public long countByCurrentHandler_IdAndDossierStatus(Long currentHandlerId, String dossierStatus) {
        return opsDossierRepository.countByCurrentHandler_IdAndDossierStatus(currentHandlerId, dossierStatus);
    }

    @Override
    public long countDelegatedDossiers(Long delegateeId, String status) {
        return opsDossierRepository.countDelegatedDossiers(delegateeId, status);
    }

    @Override
    public Double getOnTimeRateByDeptId(Long deptId) {
        return opsDossierRepository.getOnTimeRateByDeptId(deptId);
    }

    @Override
    public long countAllDossiersByDept(Long deptId) {
        return opsDossierRepository.countAllDossiersByDept(deptId);
    }

    @Override
    public long countOverdueDossiersByDept(Long deptId) {
        return opsDossierRepository.countOverdueDossiersByDept(deptId);
    }

    @Override
    public Double getAverageSatisfactionScoreByDept(Long deptId) {
        return opsDossierRepository.getAverageSatisfactionScoreByDept(deptId);
    }

    @Override
    public DelegationConfigDTO getDelegationConfigData(Long leaderId) {
        // 1. Get potential delegatees (Mock: All users in same dept except self)
        // In real app, check for 'LEADER_VICE' role
        SysUser leader = sysUserRepository.findById(leaderId).orElseThrow();
        Long deptId = leader.getDepartment().getId();

        List<String> targetRoles = java.util.List.of("CHU_TICH_UBND", "PHO_CHU_TICH_UBND");

        List<SysUser> potentialDelegatees = sysUserRepository.findPotentialDelegatees(
                deptId,
                leaderId,
                targetRoles);

        // 2. Get current delegations
        List<SysUserDelegation> currentDelegations = sysUserDelegationRepository
                .findByFromUser_IdOrderByStartTimeDesc(leaderId);

        return new DelegationConfigDTO(potentialDelegatees, currentDelegations);
    }

    @Override
    @Transactional
    public void createDelegation(Long leaderId, DelegationRequestDTO request) {
        // 1. Validation
        if (request.getDelegateeId() == null)
            throw new IllegalArgumentException("Chưa chọn người ủy quyền");
        if (request.getFromDate() == null || request.getToDate() == null)
            throw new IllegalArgumentException("Chưa chọn thời gian");

        // 2. Create Delegation
        SysUserDelegation delegation = new SysUserDelegation();
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
                    SysDelegationScope scope = new SysDelegationScope();
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

    @Override
    public void opsDossierResults(OpsDossierResult opsDossierResult) {
        opsDossierResultRepository.save(opsDossierResult);
    }

    // --- REPORTING IMPLEMENTATION ---

    @Override
    public ReportSummaryDTO getReportSummary(Long deptId, String periodType, Integer year, Integer periodValue) {
        LocalDateTime startDate;
        LocalDateTime endDate;

        LocalDate now = LocalDate.now();
        int y = (year != null) ? year : now.getYear();

        if ("MONTH".equalsIgnoreCase(periodType)) {
            int m = (periodValue != null && periodValue >= 1 && periodValue <= 12) ? periodValue : now.getMonthValue();
            startDate = LocalDate.of(y, m, 1).atStartOfDay();
            endDate = startDate.plusMonths(1).minusSeconds(1);
        } else if ("QUARTER".equalsIgnoreCase(periodType)) {
            // Quarter 1: Month 1-3, Q2: 4-6, etc.
            int currentQuarter = (now.getMonthValue() - 1) / 3 + 1;
            int quarter = (periodValue != null && periodValue >= 1 && periodValue <= 4) ? periodValue : currentQuarter;
            int startMonth = (quarter - 1) * 3 + 1;
            startDate = LocalDate.of(y, startMonth, 1).atStartOfDay();
            endDate = startDate.plusMonths(3).minusSeconds(1);
        } else { // YEAR
            startDate = LocalDate.of(y, 1, 1).atStartOfDay();
            endDate = startDate.plusYears(1).minusSeconds(1);
        }

        long received = opsDossierRepository.countReceivedInPeriod(deptId, startDate, endDate);
        long resolved = opsDossierRepository.countResolvedInPeriod(deptId, startDate, endDate);
        long onTime = opsDossierRepository.countOnTimeInPeriod(deptId, startDate, endDate);
        long rejected = opsDossierRepository.countRejectedInPeriod(deptId, startDate, endDate);

        return ReportSummaryDTO.builder()
                .totalReceived(received)
                .totalResolved(resolved)
                .onTimeCount(onTime)
                .totalRejected(rejected)
                .periodName(formatPeriodName(periodType, periodValue, y))
                .build();
    }

    @Override
    public List<ReportDomainStatDTO> getDomainStats(Long deptId, String periodType, Integer year, Integer periodValue) {
        LocalDateTime startDate;
        LocalDateTime endDate;
        LocalDate now = LocalDate.now();
        int y = (year != null) ? year : now.getYear();

        if ("MONTH".equalsIgnoreCase(periodType)) {
            int m = (periodValue != null && periodValue >= 1 && periodValue <= 12) ? periodValue : now.getMonthValue();
            startDate = LocalDate.of(y, m, 1).atStartOfDay();
            endDate = startDate.plusMonths(1).minusSeconds(1);
        } else if ("QUARTER".equalsIgnoreCase(periodType)) {
            int currentQuarter = (now.getMonthValue() - 1) / 3 + 1;
            int quarter = (periodValue != null && periodValue >= 1 && periodValue <= 4) ? periodValue : currentQuarter;
            int startMonth = (quarter - 1) * 3 + 1;
            startDate = LocalDate.of(y, startMonth, 1).atStartOfDay();
            endDate = startDate.plusMonths(3).minusSeconds(1);
        } else {
            startDate = LocalDate.of(y, 1, 1).atStartOfDay();
            endDate = startDate.plusYears(1).minusSeconds(1);
        }

        return opsDossierRepository.getStatsByDomain(deptId, startDate, endDate);
    }

    private String formatPeriodName(String type, Integer value, int year) {
        if ("MONTH".equalsIgnoreCase(type))
            return "Tháng " + value + "/" + year;
        if ("QUARTER".equalsIgnoreCase(type))
            return "Quý " + value + "/" + year;
        return "Năm " + year;
    }
}