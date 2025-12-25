package org.example.project_module4_dvc.dto.dossier;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class NewDossierDTO {
    Long id;
    BigInteger serviceId;
    String dossierCode;
    String dossierStatus;
    LocalDateTime submissionDate;
    LocalDateTime dueDate;
    String applicantFullName;
    Long applicantId;
    String serviceName;
    String cccd;
    Long specialistId;
    String rejectionReason;
    private Long receivingDeptId;
    private Map<String, Object> formData;
    String paymentStatus;

    public boolean isOverdue() {
        return dueDate != null && dueDate.isBefore(LocalDateTime.now());
    }

    public boolean isNearDue() {
        if (dueDate == null)
            return false;
        LocalDateTime now = LocalDateTime.now();
        return dueDate.isAfter(now) && dueDate.isBefore(now.plusHours(6));
    }
}
