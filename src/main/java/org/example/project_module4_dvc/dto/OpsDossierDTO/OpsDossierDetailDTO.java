package org.example.project_module4_dvc.dto.OpsDossierDTO;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.example.project_module4_dvc.entity.ops.OpsDossierFile;

/**
 * DTO để hiển thị thông tin chi tiết hồ sơ từ nhiều bảng:
 * - ops_dossiers (bảng chính)
 * - sys_users (người nộp hồ sơ, cán bộ thụ lý)
 * - cat_services (dịch vụ công)
 * - sys_departments (phòng ban)
 */
@Data
@NoArgsConstructor
public class OpsDossierDetailDTO {

    // === Thông tin từ bảng ops_dossiers ===
    private Long dossierId;
    private String dossierCode;
    private String dossierStatus;
    private LocalDateTime submissionDate;
    private LocalDateTime dueDate;
    private LocalDateTime finishDate;
    private Map<String, Object> formData;
    private String rejectionReason;
    private String resultFileUrl;
    private String decisionNumber;

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
    private List<OpsDossierFile> dossierFiles;

    /**
     * Constructor dùng cho JPQL Query (21 params)
     * Thứ tự tham số phải khớp với thứ tự SELECT trong query trong
     * OpsDossierRepository
     */
    public OpsDossierDetailDTO(
            Long dossierId,
            String dossierCode,
            String dossierStatus,
            LocalDateTime submissionDate,
            LocalDateTime dueDate,
            LocalDateTime finishDate,
            Map<String, Object> formData,
            String rejectionReason,
            Long applicantId,
            String applicantUsername,
            String applicantFullName,
            String applicantUserType,
            Long handlerId,
            String handlerUsername,
            String handlerFullName,
            Long handlerDeptId,
            String handlerDeptName,
            Long serviceId,
            String serviceName,
            String serviceCode,
            Integer processingDays,
            String decisionNumber,
            String resultFileUrl) {
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
        this.decisionNumber = decisionNumber;
        this.resultFileUrl = resultFileUrl;
    }

    /**
     * Constructor đầy đủ cho @Builder (22 params)
     */
    @Builder
    public OpsDossierDetailDTO(
            Long dossierId,
            String dossierCode,
            String dossierStatus,
            LocalDateTime submissionDate,
            LocalDateTime dueDate,
            LocalDateTime finishDate,
            Map<String, Object> formData,
            String rejectionReason,
            Long applicantId,
            String applicantUsername,
            String applicantFullName,
            String applicantUserType,
            Long handlerId,
            String handlerUsername,
            String handlerFullName,
            Long handlerDeptId,
            String handlerDeptName,
            Long serviceId,
            String serviceName,
            String serviceCode,
            Integer processingDays,
            String decisionNumber,
            String resultFileUrl,
            List<OpsDossierFile> dossierFiles) {
        this(dossierId, dossierCode, dossierStatus, submissionDate, dueDate, finishDate, formData, rejectionReason,
                applicantId, applicantUsername, applicantFullName, applicantUserType, handlerId, handlerUsername,
                handlerFullName, handlerDeptId, handlerDeptName, serviceId, serviceName, serviceCode, processingDays,
                decisionNumber, resultFileUrl);
        this.dossierFiles = dossierFiles;
    }
}
