package org.example.project_module4_dvc.service.iml;

import org.example.project_module4_dvc.dto.ChartDataDTO;
import org.example.project_module4_dvc.entity.ops.OpsDossier;

import java.util.List;
import java.util.Map;

public interface IOpsDossierService {

    Map<String, Long> getSummary();

    ChartDataDTO getChartData();

    List<OpsDossier> getOverdueDossiers();

    List<Map<String, Object>> getDossierAlerts();
}
