package org.example.project_module4_dvc.repository.ops;

import jakarta.transaction.Transactional;
import org.example.project_module4_dvc.dto.OpsDossierDTO.CitizenNotificationProjection;
import org.example.project_module4_dvc.dto.OpsDossierDTO.OpsDossierDetailDTO;
import org.example.project_module4_dvc.dto.OpsDossierDTO.OpsDossierSummaryDTO;
import org.example.project_module4_dvc.dto.leader.DossierApprovalSummaryDTO;
import org.example.project_module4_dvc.entity.ops.OpsDossier;

import org.example.project_module4_dvc.entity.sys.SysUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface OpsDossierRepository extends JpaRepository<OpsDossier, Long> {

    Page<OpsDossier> findOpsDossierByDossierStatusAndReceivingDept_DeptName(String dossierStatus, String departmentName,
            Pageable pageable);

    Page<OpsDossier> findOpsDossierByDossierStatusAndReceivingDept_DeptNameAndCurrentHandler_Id(String dossierStatus,
            String receivingDeptDeptName, Long currentHandlerId, Pageable pageable);

    @Query("""
                select hs
                from OpsDossier hs
                where hs.dueDate > :now
                  and hs.dueDate <= :limit and hs.dossierStatus = 'NEW'
                  and hs.receivingDept.deptName = :departmentName
            """)
    List<OpsDossier> findNearlyDue(
            @Param("now") LocalDateTime now,
            @Param("limit") LocalDateTime limit,
            @Param("departmentName") String departmentName);

    @Query("""
                select hs
                from OpsDossier hs
                where hs.dueDate > :now
                  and hs.dueDate <= :limit and hs.dossierStatus = 'NEW'
                  and hs.receivingDept.deptName = :departmentName
                               and  hs.currentHandler.id = :specialistId
            """)
    List<OpsDossier> findNearlyDueSpecialist(
            @Param("now") LocalDateTime now,
            @Param("limit") LocalDateTime limit,
            @Param("departmentName") String departmentName,
            @Param("specialistId") Long specialistId);

    // Tổng hồ sơ trong tháng
    @Query("""
                SELECT COUNT(d)
                FROM OpsDossier d
                WHERE MONTH(d.submissionDate) = MONTH(CURRENT_DATE)
                  AND YEAR(d.submissionDate) = YEAR(CURRENT_DATE)
            """)
    long countThisMonth();

  // Đếm theo trạng thái
  long countByDossierStatus(String dossierStatus);

    // Đếm hồ sơ quá hạn
    @Query("""
                SELECT COUNT(d)
                FROM OpsDossier d
                WHERE d.dossierStatus NOT IN ('APPROVED', 'REJECTED')
                  AND d.dueDate < CURRENT_TIMESTAMP
            """)
    long countOverdue();

    // Biểu đồ: domain + status
    @Query("""
                SELECT d.service.domain, d.dossierStatus, COUNT(d)
                FROM OpsDossier d
                GROUP BY d.service.domain, d.dossierStatus
            """)
    List<Object[]> countByDomainAndStatus();

    // Danh sách domain
    @Query("""
                SELECT DISTINCT d.service.domain
                FROM OpsDossier d
                ORDER BY d.service.domain
            """)
    List<String> findAllDomains();

  // Danh sách hồ sơ quá hạn
  @Query("SELECT d FROM OpsDossier d WHERE d.dossierStatus NOT IN ('APPROVED','REJECTED') AND d.dueDate < CURRENT_TIMESTAMP")
  List<OpsDossier> findOverdueDossiers();

  // Danh sách hồ sơ không ở trong trạng thái cho trước
  List<OpsDossier> findByDossierStatusNotIn(List<String> statusList);

  // Phân trang theo trạng thái
  Page<OpsDossier> findOpsDossierByDossierStatus(String dossierStatus, Pageable pageable);

    // Tìm hồ sơ gần đến hạn (trạng thái NEW)
    @Query("""
                select hs
                from OpsDossier hs
                where hs.dueDate > :now
                  and hs.dueDate <= :limit and hs.dossierStatus = 'NEW'
            """)
    List<OpsDossier> findNearlyDue(
            @Param("now") LocalDateTime now,
            @Param("limit") LocalDateTime limit);

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
                service.slaHours,
                res.decisionNumber,
                res.eFileUrl
            )
            FROM OpsDossier d
            JOIN d.applicant applicant
            JOIN d.service service
            LEFT JOIN d.currentHandler handler
            LEFT JOIN handler.department dept
            LEFT JOIN OpsDossierResult res ON res.dossier = d
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
                handler.fullName,
                res.id
            )
            FROM OpsDossier d
            JOIN d.applicant applicant
            JOIN d.service service
            LEFT JOIN d.currentHandler handler
            LEFT JOIN d.result res
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
                handler.fullName,
                res.id
            )
            FROM OpsDossier d
            JOIN d.applicant applicant
            JOIN d.service service
            LEFT JOIN d.currentHandler handler
            LEFT JOIN d.result res
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

    // lấy phân trang cảnh báo hồ sơ quá hạn và sắp đến hạn
    @Query(value = """
                SELECT
                    d.id AS id,
                    d.dossier_code AS code,
                    s.domain AS domain,
                    ABS(DATEDIFF(d.due_date, NOW())) AS days,
                    'OVERDUE' AS type
                FROM ops_dossiers d
                JOIN cat_services s ON d.service_id = s.id
                WHERE d.dossier_status NOT IN ('APPROVED','REJECTED')
                  AND d.due_date < NOW()
                ORDER BY d.due_date ASC
            """, countQuery = """
                SELECT COUNT(*)
                FROM ops_dossiers d
                WHERE d.dossier_status NOT IN ('APPROVED','REJECTED')
                  AND d.due_date < NOW()
            """, nativeQuery = true)
    Page<Map<String, Object>> findOverdueAlerts(Pageable pageable);

    // lấy phân trang cảnh báo hồ sơ sắp đến hạn
    @Query(value = """
                SELECT
                    d.id AS id,
                    d.dossier_code AS code,
                    s.domain AS domain,
                    DATEDIFF(d.due_date, NOW()) AS days,
                    'NEARLY_DUE' AS type
                FROM ops_dossiers d
                JOIN cat_services s ON d.service_id = s.id
                WHERE d.dossier_status NOT IN ('APPROVED','REJECTED')
                  AND d.due_date BETWEEN NOW() AND DATE_ADD(NOW(), INTERVAL 1 DAY)
                ORDER BY d.due_date ASC
            """, countQuery = """
                SELECT COUNT(*)
                FROM ops_dossiers d
                WHERE d.dossier_status NOT IN ('APPROVED','REJECTED')
                  AND d.due_date BETWEEN NOW() AND DATE_ADD(NOW(), INTERVAL 1 DAY)
            """, nativeQuery = true)
    Page<Map<String, Object>> findNearlyDueAlerts(Pageable pageable);

    // đem số hồ sơ hoàn thành
    @Query("""
                SELECT COUNT(d)
                FROM OpsDossier d
                WHERE d.dossierStatus = 'COMPLETED'
                  AND d.finishDate IS NOT NULL
            """)
    long countCompleted();

    // đêm số hồ sơ hoàn thành đúng hạn
    @Query("""
                SELECT COUNT(d)
                FROM OpsDossier d
                WHERE d.dossierStatus = 'COMPLETED'
                  AND d.finishDate IS NOT NULL
                  AND d.dueDate IS NOT NULL
                  AND d.finishDate <= d.dueDate
            """)
    long countCompletedOnTime();

    // đếm tổng số hồ sơ
    @Query("""
                SELECT COUNT(d)
                FROM OpsDossier d
                WHERE d.dossierStatus <> 'REJECTED'
            """)
    long countTotalForKpi();

    // đếm số hồ sơ hoàn thành đúng hạn
    @Query("""
                SELECT COUNT(d)
                FROM OpsDossier d
                WHERE
                    (
                        d.dossierStatus = 'COMPLETED'
                        AND d.finishDate IS NOT NULL
                        AND d.dueDate IS NOT NULL
                        AND d.finishDate <= d.dueDate
                    )
                    OR
                    (
                        d.dossierStatus <> 'COMPLETED'
                        AND d.dueDate IS NOT NULL
                        AND d.dueDate >= CURRENT_TIMESTAMP
                    )
            """)
    long countOnTimeForKpi();

  @Query(value = """
          SELECT d.dossier_code
          FROM ops_dossiers d
          WHERE d.dossier_code LIKE CONCAT(:prefix, '%')
          ORDER BY d.dossier_code DESC
          LIMIT 1
      """, nativeQuery = true)
  Optional<String> findLatestDossierCode(@Param("prefix") String prefix);

  // tìm hồ sơ theo trạng thái và mã dịch vụ bắt đầu với
  List<OpsDossier> findByDossierStatusAndServiceServiceCodeStartingWith(
      String status, String serviceCodePrefix);

  // [NEW] Optimized Query (Eager Fetch)
  @Query("SELECT d FROM OpsDossier d JOIN FETCH d.applicant JOIN FETCH d.service WHERE d.dossierStatus = :status AND d.service.serviceCode LIKE CONCAT(:serviceCodePrefix, '%')")
  List<OpsDossier> findWithRelationsByDossierStatusAndServiceServiceCodeStartingWith(
      @Param("status") String status, @Param("serviceCodePrefix") String serviceCodePrefix);

  // tìm hồ sơ theo cán bộ thụ lý hiện tại, trạng thái và mã dịch vụ bắt đầu với
  List<OpsDossier> findByCurrentHandlerIdAndDossierStatusAndServiceServiceCodeStartingWith(
      Long handlerId, String status, String serviceCodePrefix);

  // [NEW] Optimized Query with Handler (Eager Fetch)
  @Query("SELECT d FROM OpsDossier d JOIN FETCH d.applicant JOIN FETCH d.service WHERE d.currentHandler.id = :handlerId AND d.dossierStatus = :status AND d.service.serviceCode LIKE CONCAT(:serviceCodePrefix, '%')")
  List<OpsDossier> findWithRelationsByCurrentHandlerIdAndDossierStatusAndServiceServiceCodeStartingWith(
      @Param("handlerId") Long handlerId, @Param("status") String status,
      @Param("serviceCodePrefix") String serviceCodePrefix);

  // Helper to fetch with relations for detail view
  @Query("SELECT d FROM OpsDossier d JOIN FETCH d.applicant JOIN FETCH d.service LEFT JOIN FETCH d.currentHandler WHERE d.id = :id")
  Optional<OpsDossier> findWithRelationsById(@Param("id") Long id);
}