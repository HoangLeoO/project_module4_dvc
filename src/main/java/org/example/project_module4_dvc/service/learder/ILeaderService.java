package org.example.project_module4_dvc.service.learder;

import org.example.project_module4_dvc.dto.leader.DossierApprovalSummaryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ILeaderService {
    Page<DossierApprovalSummaryDTO> getMyDossiers(
            Long leaderId,
            String applicantName,
            String domain,
            Pageable pageable);

    Page<DossierApprovalSummaryDTO> getDelegatedDossiers(
            Long leaderId,
            String applicantName,
            String domain,
            Pageable pageable);

    Page<DossierApprovalSummaryDTO> findApprovedHistory(
            Long leaderId,
            String applicantName,
            String domain,
            Pageable pageable);

    void approvedByLeader(Long deptId, Long dossiersId);

    long countByCurrentHandler_IdAndDossierStatus(Long currentHandlerId, String dossierStatus);

    long countDelegatedDossiers(Long delegateeId, String status);

    Double getOnTimeRateByDeptId(Long deptId);

    long countAllDossiersByDept(Long deptId);

    long countOverdueDossiersByDept(Long deptId);

    Double getAverageSatisfactionScoreByDept(Long deptId);

    // Delegation Features
    org.example.project_module4_dvc.dto.leader.DelegationConfigDTO getDelegationConfigData(Long leaderId);
    
    void createDelegation(Long leaderId, org.example.project_module4_dvc.dto.leader.DelegationRequestDTO request);
    
    void revokeDelegation(Long delegationId);
}
