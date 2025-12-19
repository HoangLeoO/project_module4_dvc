package org.example.project_module4_dvc.dto.formData;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LandSplitMergeFormDTO {
    private String landCertificateNumber;
    private String landPlotNumber;
    private String mapSheetNumber;

    private BigDecimal originalAreaM2;
    private List<BigDecimal> requestedSplitAreas;

    private String splitReason;
    private Integer numberOfNewPlots;
    private Boolean surveyCompleted;
}
