package org.example.project_module4_dvc.dto;

/**
 * DTO dùng cho Native Query
 * 
 * Lưu ý: Khi dùng Native Query, Spring không thể dùng constructor expression
 * Cần dùng SqlResultSetMapping hoặc interface projection
 */
public interface OpsDossierNativeProjection {

    Long getDossierId();

    String getDossierCode();

    String getDossierStatus();

    String getSubmissionDate();

    // Applicant info
    String getApplicantFullName();

    String getApplicantUsername();

    // Service info
    String getServiceName();

    String getServiceCode();

    // Handler info
    String getHandlerFullName();

    String getHandlerDeptName();
}
