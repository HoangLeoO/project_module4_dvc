package org.example.project_module4_dvc.dto.leader;

public interface DossierApprovalSummaryDTO {
    Long getId();
    String getDossierCode();
    String getApplicantName();
    String getService_name();
    String getDomain();
    String getDeptName();
    String getCurrentHandlerName();
    String getDossierStatus();
    Integer getDaysLeft();
}