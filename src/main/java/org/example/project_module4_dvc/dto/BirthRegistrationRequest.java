package org.example.project_module4_dvc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BirthRegistrationRequest {

    // Service/Context
    private Long serviceId;

    // Applicant Info (Explicitly passed or strictly validated against Session)
    // For this flow, we might trust the FE to send what it knows, but logic checks
    // User Context
    private String applicantGender; // "male", "female"
    private String applicantMaritalStatus; // "single", "married"

    // Child Info
    private String childName;
    private String childGender;
    private LocalDate childDob;
    private String childEthnicity;
    private String childBirthPlace;

    // Parent Info
    private String fatherName;
    private String fatherId;
    private String motherName;
    private String motherId;

    // Logic Flags
    private boolean isPaternityRecognition; // "Nhận cha con"

    // Additional Fields for BirthRegistrationFormDTO mapping
    private String registeredAddress;
    private boolean requestBhyt;
}
