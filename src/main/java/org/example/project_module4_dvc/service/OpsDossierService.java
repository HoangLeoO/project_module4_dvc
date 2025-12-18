package org.example.project_module4_dvc.service;

import lombok.RequiredArgsConstructor;
import org.example.project_module4_dvc.dto.ChartDataDTO;
import org.example.project_module4_dvc.dto.ChartDatasetDTO;
import org.example.project_module4_dvc.dto.DossierAlertDTO;
import org.example.project_module4_dvc.entity.ops.OpsDossier;
import org.example.project_module4_dvc.repository.ops.OpsDossierRepository;
import org.example.project_module4_dvc.service.iml.IOpsDossierService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OpsDossierService implements IOpsDossierService {

    private final OpsDossierRepository opsDossierRepository;

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

        // domain -> status -> count
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
    public List<Map<String, Object>> getDossierAlerts() {
        List<OpsDossier> pendingDossiers = opsDossierRepository.findByDossierStatusNotIn(
                Arrays.asList("APPROVED", "REJECTED")
        );

        LocalDateTime now = LocalDateTime.now();
        List<Map<String, Object>> dossierAlerts = new ArrayList<>();

        for (OpsDossier d : pendingDossiers) {
            if (d.getDueDate() != null) {
                long daysDiff = ChronoUnit.DAYS.between(now.toLocalDate(), d.getDueDate().toLocalDate());

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
}
