package org.example.project_module4_dvc.dto.admin;

import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AlertDTO {

    private Long id;
    private String type;        // OVERDUE | NEARLY_DUE | FEEDBACK
    private String title;       // Mã hồ sơ / tiêu đề feedback
    private String description; // Mô tả
    private String domain;      // Lĩnh vực (nếu có)
    private Integer days;       // Số ngày (nếu có)
}
