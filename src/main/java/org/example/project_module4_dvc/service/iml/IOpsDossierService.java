package org.example.project_module4_dvc.service.iml;

import org.example.project_module4_dvc.dto.admin.ChartDataDTO;
import org.example.project_module4_dvc.entity.ops.OpsDossier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface IOpsDossierService {

    Map<String, Long> getSummary();

    ChartDataDTO getChartData();

    List<OpsDossier> getOverdueDossiers();

    Page<Map<String, Object>> getOverdueAlerts(Pageable pageable);
    Page<Map<String, Object>> getNearlyDueAlerts(Pageable pageable);
}
