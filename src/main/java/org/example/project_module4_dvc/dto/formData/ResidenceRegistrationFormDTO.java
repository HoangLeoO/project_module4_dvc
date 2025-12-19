package org.example.project_module4_dvc.dto.formData;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResidenceRegistrationFormDTO {
    private String fullName;
    private String idNumber;
    private String address;
}

