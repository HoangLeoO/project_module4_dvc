package org.example.project_module4_dvc.service.ops;

import org.example.project_module4_dvc.dto.OpsDossierDTO.CitizenNotificationProjection;
import org.example.project_module4_dvc.dto.OpsDossierDTO.OpsDossierDetailDTO;
import org.example.project_module4_dvc.dto.OpsDossierDTO.OpsDossierSummaryDTO;
import org.example.project_module4_dvc.repository.ops.OpsDossierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OpsDossierService implements IOpsDossierService {
    @Autowired
    private OpsDossierRepository opsDossierRepository;

    @Override
    public Page<OpsDossierSummaryDTO> getMyDossierList(Long userId, Pageable pageable) {
        return opsDossierRepository.findDossiersByApplicantId(userId, pageable);
    }

    @Override
    public Page<OpsDossierSummaryDTO> searchMyDossiers(Long userId, String keyword, String status, Pageable pageable) {
        String searchKeyword = (keyword != null && !keyword.trim().isEmpty()) ? "%" + keyword.trim() + "%" : null;
        String searchStatus = (status != null && !status.trim().isEmpty()) ? status.trim() : null;
        return opsDossierRepository.searchDossiersByApplicant(userId, searchKeyword, searchStatus, pageable);
    }

    @Override
    public OpsDossierDetailDTO getDossierDetail(Long id) {
        return opsDossierRepository.findDossierDetailById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ với ID: " + id));
    }

    @Override
    public Map<String, Long> getStatusCountByUser(Long userId) {
        List<Object[]> rows = opsDossierRepository.countStatusesByApplicant(userId);

        Map<String, Long> result = new HashMap<>();
        for (Object[] row : rows) {
            String status = row[0].toString();
            Long count = (Long) row[1];
            result.put(status, count);
        }
        return result;
    }

    @Override
    public List<CitizenNotificationProjection> getTop3MyNotifications(Long userId) {
        return opsDossierRepository.findTop3NotificationsByApplicant(userId);
    }

    @Override
    public Page<CitizenNotificationProjection> getAllMyNotifications(Long userId, Pageable pageable) {
        return opsDossierRepository.findAllNotificationsByApplicant(userId, pageable);
    }
}
