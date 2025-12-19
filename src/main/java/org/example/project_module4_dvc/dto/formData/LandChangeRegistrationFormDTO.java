package org.example.project_module4_dvc.dto.formData;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LandChangeRegistrationFormDTO {
    private String landCertificateNumber;
    private String landPlotNumber;
    private String landMapSheet;

    private String currentOwner;
    private String changeType;
    private String changeReason;
    private String newOwner;
}
