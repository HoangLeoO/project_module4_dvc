package org.example.project_module4_dvc.dto.formData;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeathRegistrationFormDTO {

    private String deceasedFullName;
    private LocalDate dateOfBirth;
    private String gender;
    private String ethnicity;
    private LocalDateTime dateOfDeath;
    private String placeOfDeath;
    private String deceasedIdNumber;
    private String lastResidence;
    // timeOfDeath removed

    private Boolean isOnTime;


    private String relativeFullName;
    private String relativeRelationship;
    private String relativeIdNumber;
    private String relativeDateOfBirth;
    private String relativePhoneNumber;
    private String relativeAddress;
}
