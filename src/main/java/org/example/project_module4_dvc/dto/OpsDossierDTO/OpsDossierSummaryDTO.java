package org.example.project_module4_dvc.dto.OpsDossierDTO;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO đơn giản hơn - chỉ hiển thị thông tin tóm tắt hồ sơ
 * Dùng cho danh sách (list view)
 */
@Data
@Builder
@NoArgsConstructor

public class OpsDossierSummaryDTO {
    
    private Long dossierId;
    private String dossierCode;
    private String dossierStatus;
    private LocalDateTime submissionDate;
    
    // Thông tin người nộp
    private String applicantFullName;
    
    // Thông tin dịch vụ
    private String serviceName;
    
    // Thông tin cán bộ thụ lý
    private String handlerFullName;
    
//    /**
//     * Constructor cho JPQL Query
//     */
    public OpsDossierSummaryDTO(
            Long dossierId,
            String dossierCode,
            String dossierStatus,
            LocalDateTime submissionDate,
            String applicantFullName,
            String serviceName,
            String handlerFullName
    ) {
        this.dossierId = dossierId;
        this.dossierCode = dossierCode;
        this.dossierStatus = dossierStatus;
        this.submissionDate = submissionDate;
        this.applicantFullName = applicantFullName;
        this.serviceName = serviceName;
        this.handlerFullName = handlerFullName;
    }
}
