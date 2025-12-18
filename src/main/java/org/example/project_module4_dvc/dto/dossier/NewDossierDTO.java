package org.example.project_module4_dvc.dto.dossier;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class NewDossierDTO {
    Long id;
    String dossierCode;
    String dossierStatus;
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm")
    LocalDateTime submissionDate;
    LocalDateTime dueDate;
    String applicantFullName;
    String serviceName;
    String cccd;
}
