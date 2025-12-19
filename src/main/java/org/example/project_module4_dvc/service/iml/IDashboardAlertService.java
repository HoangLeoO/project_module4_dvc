package org.example.project_module4_dvc.service.iml;

import org.example.project_module4_dvc.dto.admin.AlertDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IDashboardAlertService {
    Page<AlertDTO> getAlerts(Pageable pageable);
}