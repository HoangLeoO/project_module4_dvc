package org.example.project_module4_dvc.repository.ops;

import org.example.project_module4_dvc.dto.OpsDossierDTO.CitizenNotificationProjection;
import org.example.project_module4_dvc.dto.OpsDossierDTO.OpsDossierDetailDTO;
import org.example.project_module4_dvc.dto.OpsDossierDTO.OpsDossierSummaryDTO;
import org.example.project_module4_dvc.entity.ops.OpsDossier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
            SELECT new org.example.project_module4_dvc.dto.OpsDossierDTO.OpsDossierDetailDTO(
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
                service.slaHours
            )
            FROM OpsDossier d
            JOIN d.applicant applicant
            JOIN d.service service
            LEFT JOIN d.currentHandler handler
            LEFT JOIN handler.department dept
            WHERE d.id = :dossierId
            """)
    Optional<OpsDossierDetailDTO> findDossierDetailById(@Param("dossierId") Long dossierId);

    @Query("""
                SELECT d.dossierStatus, COUNT(d)
                FROM OpsDossier d
                WHERE d.applicant.id = :applicantId
                GROUP BY d.dossierStatus
            """)
    List<Object[]> countStatusesByApplicant(@Param("applicantId") Long applicantId);

    @Query("""
            SELECT new org.example.project_module4_dvc.dto.OpsDossierDTO.OpsDossierSummaryDTO(
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
            AND (:keyword IS NULL OR (
                LOWER(d.dossierCode) LIKE LOWER(:keyword) OR
                LOWER(service.serviceName) LIKE LOWER(:keyword)
            ))
            AND (:status IS NULL OR d.dossierStatus = :status)
            ORDER BY d.submissionDate DESC
            """)
    Page<OpsDossierSummaryDTO> searchDossiersByApplicant(
            @Param("applicantId") Long applicantId,
            @Param("keyword") String keyword,
            @Param("status") String status,
            Pageable pageable);

    @Query("""
            SELECT new org.example.project_module4_dvc.dto.OpsDossierDTO.OpsDossierSummaryDTO(
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
    Page<OpsDossierSummaryDTO> findDossiersByApplicantId(@Param("applicantId") Long applicantId, Pageable pageable);

    /**
     * đã xong/
     * 
     * //----------------------------------------------------------------------------------------------------//
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * /**
     * Tìm hồ sơ theo cán bộ thụ lý
     */
    // @Query("""
    // SELECT l FROM OpsDossierLog l
    // WHERE l.dossier.id = :dossierId
    // ORDER BY l.createdAt ASC
    // """)
    // List<OpsDossierLog> findLogsByDossierId(@Param("dossierId") Long dossierId);

    @Query(value = """
                SELECT
                    d.id AS dossierId,
                    d.dossier_code AS dossierCode,
                    l.action AS action,
                    l.comments AS message,
                    l.created_at AS createdAt
                FROM ops_dossier_logs l
                JOIN ops_dossiers d ON l.dossier_id = d.id
                WHERE d.applicant_id = :currentUserId
                ORDER BY l.created_at DESC
                LIMIT 3
            """, nativeQuery = true)
    List<CitizenNotificationProjection> findTop3NotificationsByApplicant(@Param("currentUserId") Long currentUserId);

    @Query(value = """
              SELECT
                  d.id AS dossierId,
                  d.dossier_code AS dossierCode,
                  l.action AS action,
                  l.comments AS message,
                  l.created_at AS createdAt
              FROM ops_dossier_logs l
              JOIN ops_dossiers d ON l.dossier_id = d.id
              WHERE d.applicant_id = :currentUserId
              ORDER BY l.created_at DESC
            """, countQuery = """
              SELECT COUNT(*)
              FROM ops_dossier_logs l
              JOIN ops_dossiers d ON l.dossier_id = d.id
              WHERE d.applicant_id = :currentUserId
            """, nativeQuery = true)
    Page<CitizenNotificationProjection> findAllNotificationsByApplicant(@Param("currentUserId") Long currentUserId,
            Pageable pageable);

    @Query("""
                SELECT new org.example.project_module4_dvc.dto.OpsDossierDTO.OpsDossierSummaryDTO(
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
    List<OpsDossierSummaryDTO> findDossiersByHandlerId(@Param("handlerId") Long handlerId);

    /**
     * Tìm hồ sơ theo trạng thái
     */
    @Query("""
            SELECT new org.example.project_module4_dvc.dto.OpsDossierDTO.OpsDossierSummaryDTO(
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

}
