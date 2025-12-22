package org.example.project_module4_dvc.dto.websocket;

import lombok.*;

import java.time.LocalDateTime;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class DossierItemDTO {
    private Long id;
    private String dossierCode;
    private String serviceName;
    private String applicantFullName;
    private String dossierStatus;
    private LocalDateTime submissionDate;
    private LocalDateTime dueDate;
    private Integer hoursLeft;
    private String currentHandlerName;
    private Boolean isUrgent;
}
