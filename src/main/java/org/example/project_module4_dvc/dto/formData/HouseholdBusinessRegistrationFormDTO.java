package org.example.project_module4_dvc.dto.formData;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class HouseholdBusinessRegistrationFormDTO {
    private String businessName;
    private String businessOwner;
    private String ownerIdNumber;

    private String businessAddress;
    private String businessLine;

    private BigDecimal registeredCapital;
    private Integer numberOfEmployees;

}
