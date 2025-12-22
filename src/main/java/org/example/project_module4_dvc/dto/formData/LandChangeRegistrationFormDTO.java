package org.example.project_module4_dvc.dto.formData;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LandChangeRegistrationFormDTO {
    // Owner Info
    private String currentOwner;
    private String ownerIdNumber; // From name="ownerIdNumber"
    private String ownerAddress; // From name="ownerAddress"

    // Land Asset Info
    private String landCertificateNumber;
    private String landPlotNumber;
    private String landMapSheet;

    // Change Info
    private String changeType;
    private String changeReason;
    
    // New Owner Info
    private String newOwner;
}
