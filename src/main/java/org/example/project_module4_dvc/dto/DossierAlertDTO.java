package org.example.project_module4_dvc.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DossierAlertDTO {
    private Long id;
    private String dossierCode;
    private String domain;
    private long days;  // số ngày quá hạn hoặc còn lại
    private String type; // "OVERDUE" hoặc "NEARLY_DUE"
}
