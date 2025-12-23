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
public class BirthRegistrationFormDTO {
    private String childFullName;
    private LocalDate dateOfBirth;
    private String gender;
    private String placeOfBirth;

    private String fatherFullName;
    private String fatherIdNumber;
    private Integer fatherYearOfBirth;

    private String motherFullName;
    private String motherIdNumber;
    private Integer motherYearOfBirth;

    private String registeredAddress;
    private Boolean requestBhyt;
}
