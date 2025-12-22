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
    private String gender;
    private String ethnicity;
    private LocalDate dateOfDeath;
    private String placeOfDeath;
    private String deathReason;
    private String deathNoticeNumber;
    private String lastResidence;


    private String relativeFullName;
    private String relativeRelationship;
    private String relativeIdNumber;
    private String relativeDateOfBirth;
    private String relativePhoneNumber;
    private String relativeAddress;
}
