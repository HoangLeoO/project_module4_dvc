package org.example.project_module4_dvc.dto.formData;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MarriageRegistrationFormDTO {
    private String husbandFullName;
    private LocalDate husbandDob;
    private String husbandIdNumber;

    private String wifeFullName;
    private LocalDate wifeDob;
    private String wifeIdNumber;

    private LocalDate intendedMarriageDate;
    private String registeredPlace;
}
