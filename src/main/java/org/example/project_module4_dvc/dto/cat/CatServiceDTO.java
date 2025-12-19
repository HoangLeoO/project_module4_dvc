package org.example.project_module4_dvc.dto.cat;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CatServiceDTO {
    private Long id;
    private String serviceCode;
    private String serviceName;
    private String domain;
    private Integer slaHours;
    private BigDecimal feeAmount;
    private java.util.List<org.example.project_module4_dvc.dto.timeline.IWorkflowStepProjectionDTO> steps;
}
