package org.example.project_module4_dvc.service.ops;

import org.example.project_module4_dvc.dto.OpsDossierDTO.CitizenNotificationProjection;
import org.example.project_module4_dvc.dto.OpsDossierDTO.OpsDossierDetailDTO;
import org.example.project_module4_dvc.dto.OpsDossierDTO.OpsDossierSummaryDTO;
import org.example.project_module4_dvc.dto.admin.ChartDataDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.example.project_module4_dvc.entity.ops.OpsDossier;
import java.util.List;
import java.util.Map;

public interface IOpsDossierService {
    // --- ADMIN / DASHBOARD METHODS ---
    Map<String, Long> getSummary();

    ChartDataDTO getChartData();

    List<OpsDossier> getOverdueDossiers();

    List<Map<String, Object>> getDossierAlerts();

    // --- CITIZEN METHODS ---
    // Lấy danh sách hồ sơ tóm tắt cho công dân (đang fix cứng ID=1)
    Page<OpsDossierSummaryDTO> getMyDossierList(Long userId, Pageable pageable);

    // Tìm kiếm hồ sơ của tôi
    Page<OpsDossierSummaryDTO> searchMyDossiers(Long userId, String keyword, String status, Pageable pageable);

    // Lấy chi tiết một hồ sơ theo ID
    OpsDossierDetailDTO getDossierDetail(Long id);

    // đếm số lượng trạng thái cho từng user theo id
    Map<String, Long> getStatusCountByUser(Long userId);

    // top 3 thông báo
    List<CitizenNotificationProjection> getTop3MyNotifications(Long userId);

    // lấy tất cả thông báo + phân trang
    Page<CitizenNotificationProjection> getAllMyNotifications(Long userId, Pageable pageable);


    Page<Map<String, Object>> getOverdueAlerts(Pageable pageable);
    Page<Map<String, Object>> getNearlyDueAlerts(Pageable pageable);

    int getOnTimeRate();

    int calculateOnTimeRateStrict();

    Page<OpsDossier> getAdminDossierPage(Pageable pageable);
}
