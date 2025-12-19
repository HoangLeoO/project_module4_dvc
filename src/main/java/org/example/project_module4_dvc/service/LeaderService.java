package org.example.project_module4_dvc.service;

import org.example.project_module4_dvc.dto.leader.DossierApprovalSummaryDTO;
import org.example.project_module4_dvc.repository.leader.LeaderOpsDossierRepository;
import org.example.project_module4_dvc.repository.ops.OpsDossierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class LeaderService implements ILeaderService{

    @Autowired
    private LeaderOpsDossierRepository opsDossierRepository;


    @Override
    public Page<DossierApprovalSummaryDTO> getMyDossiers(Long leaderId, String applicantName, String domain, Pageable pageable) {
        return opsDossierRepository.findMyPendingDossiers(leaderId,applicantName,domain,pageable);
    }

    @Override
    public Page<DossierApprovalSummaryDTO> getDelegatedDossiers(Long leaderId, String applicantName, String domain, Pageable pageable) {
        return opsDossierRepository.findDelegatedPendingDossiers(leaderId,applicantName,domain,pageable);
    }

    @Override
    public void approvedByLeader(Long userId,Long dossiersId) {
        opsDossierRepository.updateStatusApprovedDossier(userId,dossiersId);
    }

    @Override
    public long countByCurrentHandler_IdAndDossierStatus(Long currentHandlerId, String dossierStatus) {
        return opsDossierRepository.countByCurrentHandler_IdAndDossierStatus(currentHandlerId,dossierStatus);
    }

    @Override
    public long countDelegatedDossiers(Long delegateeId, String status) {
        return opsDossierRepository.countDelegatedDossiers(delegateeId,status);
    }


}
