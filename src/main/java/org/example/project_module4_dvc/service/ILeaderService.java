package org.example.project_module4_dvc.service;

import org.example.project_module4_dvc.dto.leader.DossierApprovalSummaryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ILeaderService {
    Page<DossierApprovalSummaryDTO> getMyDossiers(Long leaderId,String applicantName, String domain, Pageable pageable);
    Page<DossierApprovalSummaryDTO> getDelegatedDossiers(Long leaderId,String applicantName, String domain, Pageable pageable);

    void approvedByLeader(Long deptId,Long dossiersId);
}
