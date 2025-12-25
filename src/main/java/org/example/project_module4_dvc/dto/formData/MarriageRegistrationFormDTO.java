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
public class MarriageRegistrationFormDTO {
    private String husbandFullName;
    private LocalDate husbandDob;
    private String husbandIdNumber;
    private String husbandGender;

    private String wifeFullName;
    private LocalDate wifeDob;
    private String wifeIdNumber;
    private String wifeGender;

    private LocalDate intendedMarriageDate;
    private String registeredPlace;
}
