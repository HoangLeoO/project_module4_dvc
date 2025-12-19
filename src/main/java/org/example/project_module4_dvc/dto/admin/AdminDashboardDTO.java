package org.example.project_module4_dvc.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminDashboardDTO {
    // Tổng số cảnh báo vận hành cho admin
    private int totalAlerts;

    // Số hồ sơ dịch vụ công quá hạn xử lý
    private int overdueServiceDossiers;

    // Số phản ánh / góp ý của công dân chưa được trả lời
    private int pendingCitizenFeedbacks;

    // 2. Tổng quan hồ sơ
    private Map<String, Long> summaryByStatus;

    // 3. Dữ liệu chart
    private ChartDataDTO chartData;
}
