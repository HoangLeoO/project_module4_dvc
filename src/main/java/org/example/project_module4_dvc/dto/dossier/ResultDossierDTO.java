package org.example.project_module4_dvc.dto.dossier;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ResultDossierDTO {
    Long id;
    String dossierCode;
    String dossierStatus;
    LocalDateTime submissionDate;
    LocalDateTime dueDate;

    LocalDateTime finishDate;
    String applicantFullName;
    String serviceName;
    String cccd;
    String decisionNumber;
    String signerName;

    String eFileUrl;
}
