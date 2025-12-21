package org.example.project_module4_dvc.dto.leader;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
//@AllArgsConstructor
public class DossierApprovalSummaryDTO {
    private Long id;
    private String dossierCode;
    private String applicantName;
    private String service_name;
    private String domain;
    private String deptName;
    private String currentHandlerName;
    private String dossierStatus;
    private LocalDateTime finishDate;
    private Long daysLeft; // Changed to Long for calculation flexibility

    // Constructor for JPQL (Finish Date can be null, Days Left calculation logic can be handled here or in query)
    public DossierApprovalSummaryDTO(Long id, String dossierCode, String applicantName, String service_name, String domain, String deptName, String currentHandlerName, String dossierStatus, LocalDateTime finishDate) {
        this.id = id;
        this.dossierCode = dossierCode;
        this.applicantName = applicantName;
        this.service_name = service_name;
        this.domain = domain;
        this.deptName = deptName;
        this.currentHandlerName = currentHandlerName;
        this.dossierStatus = dossierStatus;
        this.finishDate = finishDate;
    }
    
    // Constructor matching the fields used in JPQL
    public DossierApprovalSummaryDTO(Long id, String dossierCode, String applicantName, String service_name, String domain, String deptName, String currentHandlerName, String dossierStatus, LocalDateTime finishDate, Long daysLeft) {
        this.id = id;
        this.dossierCode = dossierCode;
        this.applicantName = applicantName;
        this.service_name = service_name;
        this.domain = domain;
        this.deptName = deptName;
        this.currentHandlerName = currentHandlerName;
        this.dossierStatus = dossierStatus;
        this.finishDate = finishDate;
        this.daysLeft = daysLeft;
    }
}