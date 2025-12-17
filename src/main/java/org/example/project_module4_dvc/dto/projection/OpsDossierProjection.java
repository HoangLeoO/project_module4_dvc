package org.example.project_module4_dvc.dto.projection;

import java.time.LocalDateTime;

/**
 * CÁCH 2: Interface-based Projection
 * 
 * Spring Data JPA sẽ tự động implement interface này
 * Chỉ cần định nghĩa getter methods
 * 
 * Ưu điểm:
 * - Code ngắn gọn hơn
 * - Không cần constructor
 * - Spring tự động map
 * 
 * Nhược điểm:
 * - Ít linh hoạt hơn Constructor-based
 * - Khó debug hơn
 */
public interface OpsDossierProjection {

    // === Từ OpsDossier ===
    Long getId();

    String getDossierCode();

    String getDossierStatus();

    LocalDateTime getSubmissionDate();

    // === Nested projection cho Applicant ===
    ApplicantProjection getApplicant();

    interface ApplicantProjection {
        Long getId();

        String getUsername();

        String getFullName();

        String getUserType();
    }

    // === Nested projection cho Service ===
    ServiceProjection getService();

    interface ServiceProjection {
        Long getId();

        String getServiceName();

        String getServiceCode();

        Integer getProcessingDays();
    }

    // === Nested projection cho Handler ===
    HandlerProjection getCurrentHandler();

    interface HandlerProjection {
        Long getId();

        String getFullName();

        // Nested cho Department
        DepartmentProjection getDepartment();

        interface DepartmentProjection {
            Long getId();

            String getDeptName();
        }
    }
}
