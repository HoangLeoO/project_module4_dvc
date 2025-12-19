package org.example.project_module4_dvc.dto.formData;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HouseholdBusinessRegistrationFormDTO {
    private String businessName;
    private String businessOwner;
    private String ownerIdNumber;

    private String businessAddress;
    private String businessLine;

    private BigDecimal registeredCapital;
    private Integer numberOfEmployees;

}
