package org.example.project_module4_dvc.dto.formData;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LandPurposeChangeFormDTO {
    private String landCertificateNumber;
    private String landPlotNumber;
    private String mapSheetNumber;

    private String currentLandPurpose;
    private String requestedLandPurpose;

    private BigDecimal landAreaM2;
    private String reasonForChange;
    private String commitment;
}
