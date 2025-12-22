package org.example.project_module4_dvc.dto.formData;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MaritalStatusCertificateFormDTO {

    private String requesterFullName;
    private LocalDate dateOfBirth;
    private String idNumber;

    private String currentMaritalStatus;
    private String confirmationPeriod;
    private String purposeOfUse;
}
