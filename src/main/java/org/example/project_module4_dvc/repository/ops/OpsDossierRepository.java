package org.example.project_module4_dvc.repository.ops;

import org.example.project_module4_dvc.dto.OpsDossierDetailDTO;
import org.example.project_module4_dvc.dto.OpsDossierSummaryDTO;
import org.example.project_module4_dvc.entity.ops.OpsDossier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OpsDossierRepository extends JpaRepository<OpsDossier, Long> {

    /**
     * CÁCH 1: Constructor-based Projection với JPQL
     * Lấy thông tin chi tiết hồ sơ từ nhiều bảng
     * 
     * Ưu điểm:
     * - Chỉ SELECT các field cần thiết (không load toàn bộ entity)
     * - Hiệu suất tốt
     * - Type-safe
     */
    @Query("""
            SELECT new org.example.project_module4_dvc.dto.OpsDossierDetailDTO(
                d.id,
                d.dossierCode,
                d.dossierStatus,
                d.submissionDate,
                d.dueDate,
                d.finishDate,
                d.formData,
                d.rejectionReason,
                applicant.id,
                applicant.username,
                applicant.fullName,
                applicant.userType,
                handler.id,
                handler.username,
                handler.fullName,
                dept.id,
                dept.deptName,
                service.id,
                service.serviceName,
                service.serviceCode,
                service.processingDays
            )
            FROM OpsDossier d
            JOIN d.applicant applicant
            JOIN d.service service
            LEFT JOIN d.currentHandler handler
            LEFT JOIN handler.department dept
            WHERE d.id = :dossierId
            """)
    Optional<OpsDossierDetailDTO> findDossierDetailById(@Param("dossierId") Long dossierId);

    /**
     * Lấy danh sách hồ sơ tóm tắt (cho list view)
     */
    @Query("""
            SELECT new org.example.project_module4_dvc.dto.OpsDossierSummaryDTO(
                d.id,
                d.dossierCode,
                d.dossierStatus,
                d.submissionDate,
                applicant.fullName,
                service.serviceName,
                handler.fullName
            )
            FROM OpsDossier d
            JOIN d.applicant applicant
            JOIN d.service service
            LEFT JOIN d.currentHandler handler
            ORDER BY d.submissionDate DESC
            """)
    List<OpsDossierSummaryDTO> findAllDossierSummaries();

    /**
     * Tìm hồ sơ theo người nộp
     */
    @Query("""
            SELECT new org.example.project_module4_dvc.dto.OpsDossierSummaryDTO(
                d.id,
                d.dossierCode,
                d.dossierStatus,
                d.submissionDate,
                applicant.fullName,
                service.serviceName,
                handler.fullName
            )
            FROM OpsDossier d
            JOIN d.applicant applicant
            JOIN d.service service
            LEFT JOIN d.currentHandler handler
            WHERE applicant.id = :applicantId
            ORDER BY d.submissionDate DESC
            """)
    List<OpsDossierSummaryDTO> findDossiersByApplicantId(@Param("applicantId") Long applicantId);

    /**
     * Tìm hồ sơ theo cán bộ thụ lý
     */
    @Query("""
            SELECT new org.example.project_module4_dvc.dto.OpsDossierSummaryDTO(
                d.id,
                d.dossierCode,
                d.dossierStatus,
                d.submissionDate,
                applicant.fullName,
                service.serviceName,
                handler.fullName
            )
            FROM OpsDossier d
            JOIN d.applicant applicant
            JOIN d.service service
            LEFT JOIN d.currentHandler handler
            WHERE handler.id = :handlerId
            ORDER BY d.submissionDate DESC
            """)
    List<OpsDossierSummaryDTO> findDossiersByHandlerId(@Param("handlerId") Long handlerId);

    /**
     * Tìm hồ sơ theo trạng thái
     */
    @Query("""
            SELECT new org.example.project_module4_dvc.dto.OpsDossierSummaryDTO(
                d.id,
                d.dossierCode,
                d.dossierStatus,
                d.submissionDate,
                applicant.fullName,
                service.serviceName,
                handler.fullName
            )
            FROM OpsDossier d
            JOIN d.applicant applicant
            JOIN d.service service
            LEFT JOIN d.currentHandler handler
            WHERE d.dossierStatus = :status
            ORDER BY d.submissionDate DESC
            """)
    List<OpsDossierSummaryDTO> findDossiersByStatus(@Param("status") String status);

    /**
     * VÍ DỤ: Sử dụng Native Query với Interface Projection
     * 
     * Khi nào dùng Native Query:
     * - Cần tối ưu performance với SQL phức tạp
     * - Dùng database-specific features (window functions, CTEs, ...)
     * - JPQL không đủ mạnh
     * 
     * Lưu ý: Phải dùng alias khớp với tên getter trong interface
     * Ví dụ: getDossierId() -> alias là "dossierId"
     */
    @Query(value = """
            SELECT
                d.id AS dossierId,
                d.dossier_code AS dossierCode,
                d.dossier_status AS dossierStatus,
                d.submission_date AS submissionDate,
                applicant.full_name AS applicantFullName,
                applicant.username AS applicantUsername,
                service.service_name AS serviceName,
                service.service_code AS serviceCode,
                handler.full_name AS handlerFullName,
                dept.dept_name AS handlerDeptName
            FROM ops_dossiers d
            INNER JOIN sys_users applicant ON d.applicant_id = applicant.id
            INNER JOIN cat_services service ON d.service_id = service.id
            LEFT JOIN sys_users handler ON d.current_handler_id = handler.id
            LEFT JOIN sys_departments dept ON handler.dept_id = dept.id
            WHERE d.id = :dossierId
            """, nativeQuery = true)
    Optional<org.example.project_module4_dvc.dto.OpsDossierNativeProjection> findDossierByIdNative(
            @Param("dossierId") Long dossierId);
}
