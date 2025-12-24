package org.example.project_module4_dvc.dto.leader.report;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportSummaryDTO {
    private long totalReceived;
    private long totalResolved;
    private long onTimeCount;
    private long totalRejected;
    private String periodName; // e.g. "Tháng 12/2023"

    public Double getOnTimeRate() {
        if (totalResolved == 0) return 100.0;
        return Math.round((double) onTimeCount * 100.0 / totalResolved * 10.0) / 10.0;
    }
}
