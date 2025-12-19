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
public class DeathRegistrationFormDTO {
     private String deceasedFullName;
    private LocalDate dateOfBirth;
    private LocalDate dateOfDeath;
    private String placeOfDeath;

    private String lastResidence;

    private String relativeFullName;
    private String relativeRelationship;
}
