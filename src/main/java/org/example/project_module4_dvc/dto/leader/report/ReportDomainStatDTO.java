package org.example.project_module4_dvc.dto.leader.report;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportDomainStatDTO {
    private String domain;
    private Long totalReceived;
    private Long processing;
    private Long completed;
    private Long onTimeCount;
    
    // Calculated field for UI
    public Double getOnTimeRate() {
        if (completed == null || completed == 0) return 100.0;
        return Math.round((double) onTimeCount * 100.0 / completed * 10.0) / 10.0;
    }
}
