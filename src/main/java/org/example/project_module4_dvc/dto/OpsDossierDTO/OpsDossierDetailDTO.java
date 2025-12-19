package org.example.project_module4_dvc.dto.OpsDossierDTO;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO để hiển thị thông tin chi tiết hồ sơ từ nhiều bảng:
 * - ops_dossiers (bảng chính)
 * - sys_users (người nộp hồ sơ, cán bộ thụ lý)
 * - cat_services (dịch vụ công)
 * - sys_departments (phòng ban)
 */
@Data
@Builder
@NoArgsConstructor
public class OpsDossierDetailDTO {

    // === Thông tin từ bảng ops_dossiers ===
    private Long dossierId;
    private String dossierCode;
    private String dossierStatus;
    private LocalDateTime submissionDate;
    private LocalDateTime dueDate;
    private LocalDateTime finishDate;
    private Map<String,Object> formData;
    private String rejectionReason;

    // === Thông tin người nộp hồ sơ (từ bảng sys_users) ===
    private Long applicantId;
    private String applicantUsername;
    private String applicantFullName;
    private String applicantUserType;

    // === Thông tin cán bộ thụ lý (từ bảng sys_users) ===
    private Long handlerId;
    private String handlerUsername;
    private String handlerFullName;

    // === Thông tin phòng ban của cán bộ (từ bảng sys_departments) ===
    private Long handlerDeptId;
    private String handlerDeptName;

    // === Thông tin dịch vụ công (từ bảng cat_services) ===
    private Long serviceId;
    private String serviceName;
    private String serviceCode;
    private Integer processingDays;

    /**
     * Constructor dùng cho JPQL Query
     * Thứ tự tham số phải khớp với thứ tự SELECT trong query
     */
    public OpsDossierDetailDTO(
            // Từ OpsDossier
            Long dossierId,
            String dossierCode,
            String dossierStatus,
            LocalDateTime submissionDate,
            LocalDateTime dueDate,
            LocalDateTime finishDate,
            Map<String,Object> formData,
            String rejectionReason,
            // Từ SysUser (applicant)
            Long applicantId,
            String applicantUsername,
            String applicantFullName,
            String applicantUserType,
            // Từ SysUser (handler)
            Long handlerId,
            String handlerUsername,
            String handlerFullName,
            // Từ SysDepartment
            Long handlerDeptId,
            String handlerDeptName,
            // Từ CatService
            Long serviceId,
            String serviceName,
            String serviceCode,
            Integer processingDays) {
        this.dossierId = dossierId;
        this.dossierCode = dossierCode;
        this.dossierStatus = dossierStatus;
        this.submissionDate = submissionDate;
        this.dueDate = dueDate;
        this.finishDate = finishDate;
        this.formData = formData;
        this.rejectionReason = rejectionReason;
        this.applicantId = applicantId;
        this.applicantUsername = applicantUsername;
        this.applicantFullName = applicantFullName;
        this.applicantUserType = applicantUserType;
        this.handlerId = handlerId;
        this.handlerUsername = handlerUsername;
        this.handlerFullName = handlerFullName;
        this.handlerDeptId = handlerDeptId;
        this.handlerDeptName = handlerDeptName;
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.serviceCode = serviceCode;
        this.processingDays = processingDays;
    }
}
