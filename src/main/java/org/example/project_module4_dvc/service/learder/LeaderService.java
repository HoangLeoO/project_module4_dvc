package org.example.project_module4_dvc.service.learder;

import jakarta.transaction.Transactional;
import org.example.project_module4_dvc.dto.leader.DelegationConfigDTO;
import org.example.project_module4_dvc.dto.leader.DelegationRequestDTO;
import org.example.project_module4_dvc.dto.leader.DossierApprovalSummaryDTO;
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

    @Override
    public Page<DossierApprovalSummaryDTO> getMyDossiers(Long leaderId, String applicantName, String domain, Pageable pageable) {
        return opsDossierRepository.findMyPendingDossiers(leaderId, applicantName, domain, pageable);
    }

    @Override
    public Page<DossierApprovalSummaryDTO> getDelegatedDossiers(Long leaderId, String applicantName, String domain, Pageable pageable) {
        return opsDossierRepository.findDelegatedPendingDossiers(leaderId, applicantName, domain, pageable);
    }

    @Override
    public Page<DossierApprovalSummaryDTO> findApprovedHistory(Long leaderId, String applicantName, String domain, Pageable pageable) {
        return opsDossierRepository.findApprovedHistory(leaderId, applicantName, domain, pageable);
    }

    @Override
    public void approvedByLeader(Long userId, Long dossiersId) {
        opsDossierRepository.updateStatusApprovedDossier(userId, dossiersId);
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
                targetRoles
        );

        // 2. Get current delegations
        List<SysUserDelegation> currentDelegations =
                sysUserDelegationRepository.findByFromUser_IdOrderByStartTimeDesc(leaderId);

        return new DelegationConfigDTO(potentialDelegatees, currentDelegations);
    }

    @Override
    @Transactional
    public void createDelegation(Long leaderId, DelegationRequestDTO request) {
        // 1. Validation
        if (request.getDelegateeId() == null) throw new IllegalArgumentException("Chưa chọn người ủy quyền");
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
}