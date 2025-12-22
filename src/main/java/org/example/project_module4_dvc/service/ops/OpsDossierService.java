package org.example.project_module4_dvc.service.ops;

import org.example.project_module4_dvc.dto.OpsDossierDTO.CitizenNotificationProjection;
import org.example.project_module4_dvc.dto.OpsDossierDTO.OpsDossierDetailDTO;
import org.example.project_module4_dvc.dto.OpsDossierDTO.OpsDossierSummaryDTO;
import org.example.project_module4_dvc.dto.admin.ChartDataDTO;
import org.example.project_module4_dvc.dto.admin.ChartDatasetDTO;
import org.example.project_module4_dvc.entity.ops.OpsDossier;
import org.example.project_module4_dvc.repository.ops.OpsDossierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    // ==========================================
    // merged from service.OpsDossierService
    // ==========================================



    @Override
    public List<Map<String, Object>> getDossierAlerts() {
        List<org.example.project_module4_dvc.entity.ops.OpsDossier> pendingDossiers = opsDossierRepository.findByDossierStatusNotIn(
                java.util.Arrays.asList("APPROVED", "REJECTED")
        );

        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        List<Map<String, Object>> dossierAlerts = new java.util.ArrayList<>();

        for (org.example.project_module4_dvc.entity.ops.OpsDossier d : pendingDossiers) {
            if (d.getDueDate() != null) {
                long daysDiff = java.time.temporal.ChronoUnit.DAYS.between(now.toLocalDate(), d.getDueDate().toLocalDate());

                Map<String, Object> alert = new HashMap<>();
                alert.put("id", d.getId());
                alert.put("code", d.getDossierCode());
                alert.put("domain", d.getService().getDomain());

                if (daysDiff < 0) { // Quá hạn
                    alert.put("type", "OVERDUE");
                    alert.put("days", Math.abs(daysDiff));
                } else if (daysDiff <= 3) { // Sắp đến hạn trong 3 ngày
                    alert.put("type", "NEARLY_DUE");
                    alert.put("days", daysDiff);
                } else {
                    continue; // Không cần cảnh báo
                }

                dossierAlerts.add(alert);
            }
        }
        return dossierAlerts;
    }

    // ===== SUMMARY =====
    @Override
    public Map<String, Long> getSummary() {

        Map<String, Long> map = new HashMap<>();
        map.put("total", opsDossierRepository.countThisMonth());
        map.put("processing", opsDossierRepository.countByDossierStatus("PENDING"));
        map.put("completed", opsDossierRepository.countByDossierStatus("APPROVED"));
        map.put("overdue", opsDossierRepository.countOverdue());

        return map;
    }

    // ===== CHART =====
    @Override
    public ChartDataDTO getChartData() {

        List<String> domains = opsDossierRepository.findAllDomains();
        List<String> statuses = List.of("NEW", "PENDING", "APPROVED", "REJECTED");

        List<Object[]> raw = opsDossierRepository.countByDomainAndStatus();

//         domain -> status -> count
        Map<String, Map<String, Long>> map = new HashMap<>();

        for (Object[] r : raw) {
            String domain = (String) r[0];
            String status = (String) r[1];
            Long count = (Long) r[2];

            map.computeIfAbsent(domain, k -> new HashMap<>())
                    .put(status, count);
        }

        List<ChartDatasetDTO> datasets = new ArrayList<>();

        for (String status : statuses) {
            List<Long> data = new ArrayList<>();
            for (String domain : domains) {
                data.add(
                        map.getOrDefault(domain, Map.of())
                                .getOrDefault(status, 0L)
                );
            }
            datasets.add(new ChartDatasetDTO(status, data));
        }

        return new ChartDataDTO(domains, datasets);
    }

    @Override
    public List<OpsDossier> getOverdueDossiers() {
        return opsDossierRepository.findOverdueDossiers();
    }

    @Override
    public Page<Map<String, Object>> getOverdueAlerts(Pageable pageable) {
        return opsDossierRepository.findOverdueAlerts(pageable);
    }

    @Override
    public Page<Map<String, Object>> getNearlyDueAlerts(Pageable pageable) {
        return opsDossierRepository.findNearlyDueAlerts(pageable);
    }

    @Override
    public int getOnTimeRate() {
        long completed = opsDossierRepository.countCompleted();

        if (completed == 0) {
            return 100; // hoặc 0 tùy nghiệp vụ
        }

        long onTime = opsDossierRepository.countCompletedOnTime();

        return Math.round((onTime * 100f) / completed);
    }

    @Override
    public int calculateOnTimeRateStrict() {

        long total = opsDossierRepository.countThisMonth();
        if (total == 0) {
            return 100;
        }

        long onTime = opsDossierRepository.countOverdue();

        return Math.round(((total - onTime) * 100f) / total);
    }

    @Override
    public Page<OpsDossier> getAdminDossierPage(Pageable pageable) {
        return opsDossierRepository.findAll(pageable);
    }
}