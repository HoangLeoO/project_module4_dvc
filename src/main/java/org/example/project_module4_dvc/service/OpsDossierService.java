package org.example.project_module4_dvc.service;

import lombok.RequiredArgsConstructor;
import org.example.project_module4_dvc.dto.admin.ChartDataDTO;
import org.example.project_module4_dvc.dto.admin.ChartDatasetDTO;
import org.example.project_module4_dvc.entity.ops.OpsDossier;
import org.example.project_module4_dvc.repository.ops.OpsDossierRepository;
import org.example.project_module4_dvc.service.iml.IOpsDossierService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

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
    public Page<Map<String, Object>> getOverdueAlerts(Pageable pageable) {
        return opsDossierRepository.findOverdueAlerts(pageable);
    }

    @Override
    public Page<Map<String, Object>> getNearlyDueAlerts(Pageable pageable) {
        return opsDossierRepository.findNearlyDueAlerts(pageable);
    }
}
