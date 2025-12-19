package org.example.project_module4_dvc.repository.ops;

import org.example.project_module4_dvc.dto.OpsDossierDTO.CitizenNotificationProjection;
import org.example.project_module4_dvc.dto.OpsDossierDTO.OpsDossierDetailDTO;
import org.example.project_module4_dvc.dto.OpsDossierDTO.OpsDossierSummaryDTO;
import jakarta.transaction.Transactional;
import org.example.project_module4_dvc.dto.leader.DossierApprovalSummaryDTO;
import org.example.project_module4_dvc.entity.ops.OpsDossier;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import java.time.LocalDateTime;
import java.util.List;

import java.time.LocalDateTime;
import java.util.Map;


@Repository
public interface OpsDossierRepository extends JpaRepository<OpsDossier, Long> {

    // =========================================================================
    // 1. QUERY HỒ SƠ CỦA TÔI (Gán trực tiếp cho Leader ID)
    // =========================================================================
    @Query(value = """
        SELECT 
            d.id AS id,
            d.dossier_code AS dossierCode,
            u_applicant.full_name AS applicantName,
            s.service_name AS service_name,
            s.domain AS domain,
            dept.dept_name AS deptName,
            u_handler.full_name AS currentHandlerName,
            d.dossier_status AS dossierStatus,
            DATEDIFF(d.due_date, NOW()) AS daysLeft
        FROM ops_dossiers d
        JOIN cat_services s ON d.service_id = s.id
        JOIN sys_departments dept ON d.receiving_dept_id = dept.id
        JOIN sys_users u_applicant ON d.applicant_id = u_applicant.id
        JOIN sys_users u_handler ON d.current_handler_id = u_handler.id
        WHERE d.dossier_status = 'VERIFIED' 
          -- ĐIỀU KIỆN: Người giữ hồ sơ CHÍNH LÀ tôi
          AND d.current_handler_id = :leaderId
          
          -- Bộ lọc
          AND (:applicantName IS NULL OR u_applicant.full_name LIKE CONCAT('%', :applicantName, '%'))
          AND (:domain IS NULL OR s.domain = :domain)
        ORDER BY d.due_date ASC
        """,
            countQuery = """
        SELECT COUNT(d.id)
        FROM ops_dossiers d
        JOIN cat_services s ON d.service_id = s.id
        JOIN sys_users u_applicant ON d.applicant_id = u_applicant.id
        WHERE d.dossier_status = 'VERIFIED'
          AND d.current_handler_id = :leaderId
          AND (:applicantName IS NULL OR u_applicant.full_name LIKE CONCAT('%', :applicantName, '%'))
          AND (:domain IS NULL OR s.domain = :domain)
        """,
            nativeQuery = true)
    Page<DossierApprovalSummaryDTO> findMyPendingDossiers(
            @Param("leaderId") Long leaderId,
            @Param("applicantName") String applicantName,
            @Param("domain") String domain,
            Pageable pageable);


    // =========================================================================
    // 2. QUERY HỒ SƠ ỦY QUYỀN (Gán cho người khác, nhưng tôi được quyền xử lý)
    // =========================================================================
    @Query(value = """
        SELECT 
            d.id AS id,
            d.dossier_code AS dossierCode,
            u_applicant.full_name AS applicantName,
            s.service_name AS service_name,
            s.domain AS domain,
            dept.dept_name AS deptName,
            u_handler.full_name AS currentHandlerName, -- Sẽ hiện tên người ủy quyền (VD: Chủ tịch)
            d.dossier_status AS dossierStatus,
            DATEDIFF(d.due_date, NOW()) AS daysLeft
        FROM ops_dossiers d
        JOIN cat_services s ON d.service_id = s.id
        JOIN sys_departments dept ON d.receiving_dept_id = dept.id
        JOIN sys_users u_applicant ON d.applicant_id = u_applicant.id
        JOIN sys_users u_handler ON d.current_handler_id = u_handler.id
        WHERE d.dossier_status = 'VERIFIED' 
          
          -- ĐIỀU KIỆN: Người giữ hồ sơ nằm trong danh sách những người đã ủy quyền cho tôi
          AND d.current_handler_id IN (
                SELECT dlg.from_user_id 
                FROM sys_user_delegations dlg
                WHERE dlg.to_user_id = :leaderId      -- Tôi là người nhận ủy quyền
                  AND NOW() BETWEEN dlg.start_time AND dlg.end_time -- Còn hiệu lực
          )
          
          -- Bộ lọc
          AND (:applicantName IS NULL OR u_applicant.full_name LIKE CONCAT('%', :applicantName, '%'))
          AND (:domain IS NULL OR s.domain = :domain)
        ORDER BY d.due_date ASC
        """,
            countQuery = """
        SELECT COUNT(d.id)
        FROM ops_dossiers d
        JOIN cat_services s ON d.service_id = s.id
        JOIN sys_users u_applicant ON d.applicant_id = u_applicant.id
        WHERE d.dossier_status = 'VERIFIED'
          AND d.current_handler_id IN (
                SELECT dlg.from_user_id 
                FROM sys_user_delegations dlg
                WHERE dlg.to_user_id = :leaderId
                  AND NOW() BETWEEN dlg.start_time AND dlg.end_time
          )
          AND (:applicantName IS NULL OR u_applicant.full_name LIKE CONCAT('%', :applicantName, '%'))
          AND (:domain IS NULL OR s.domain = :domain)
        """,
            nativeQuery = true)
    Page<DossierApprovalSummaryDTO> findDelegatedPendingDossiers(
            @Param("leaderId") Long leaderId,
            @Param("applicantName") String applicantName,
            @Param("domain") String domain,
            Pageable pageable);

    @Transactional
    @Modifying
    @Query(value = "UPDATE ops_dossiers " +
            "SET " +
            "    dossier_status = 'APPROVED', " +
            "    finish_date = NOW(), " +
            "    current_handler_id = :leader_id " +
            "WHERE id = :dossier_id", nativeQuery = true)
    void updateStatusApprovedDossier(@Param("leader_id") Long leader_id, @Param("dossier_id") Long dossier_id);

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
            @Param("limit") LocalDateTime limit
    );



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
""",
            countQuery = """
    SELECT COUNT(*)
    FROM ops_dossiers d
    WHERE d.dossier_status NOT IN ('APPROVED','REJECTED')
      AND d.due_date < NOW()
""",
            nativeQuery = true)
    Page<Map<String, Object>> findOverdueAlerts(Pageable pageable);

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
      AND d.due_date BETWEEN NOW() AND DATE_ADD(NOW(), INTERVAL 3 DAY)
    ORDER BY d.due_date ASC
""",
            countQuery = """
    SELECT COUNT(*)
    FROM ops_dossiers d
    WHERE d.dossier_status NOT IN ('APPROVED','REJECTED')
      AND d.due_date BETWEEN NOW() AND DATE_ADD(NOW(), INTERVAL 3 DAY)
""",
            nativeQuery = true)
    Page<Map<String, Object>> findNearlyDueAlerts(Pageable pageable);
}
