package org.example.project_module4_dvc.repository.leader;

import jakarta.transaction.Transactional;
import org.example.project_module4_dvc.dto.leader.DossierApprovalSummaryDTO;
import org.example.project_module4_dvc.entity.ops.OpsDossier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaderOpsDossierRepository extends JpaRepository<OpsDossier, Long> {
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
          -- VÀ thỏa mãn phạm vi ủy quyền (nếu có)
          AND EXISTS (
                SELECT 1 
                FROM sys_user_delegations dlg
                WHERE dlg.from_user_id = d.current_handler_id -- Người đang giữ hồ sơ là người ủy quyền
                  AND dlg.to_user_id = :leaderId              -- Tôi là người nhận
                  AND NOW() BETWEEN dlg.start_time AND dlg.end_time
                  
                  -- CHECK SCOPE
                  AND (
                      -- Trường hợp 1: Không có scope nào (Ủy quyền toàn bộ)
                      NOT EXISTS (SELECT 1 FROM sys_delegation_scopes sc WHERE sc.delegation_id = dlg.id)
                      
                      OR 
                      
                      -- Trường hợp 2: Có scope khớp với DOMAIN
                      EXISTS (
                          SELECT 1 FROM sys_delegation_scopes sc 
                          WHERE sc.delegation_id = dlg.id 
                            AND sc.scope_type = 'DOMAIN' 
                            AND sc.scope_value = s.domain
                      )
                      
                      OR
                      
                      -- Trường hợp 3: Có scope khớp với SERVICE_CODE
                      EXISTS (
                          SELECT 1 FROM sys_delegation_scopes sc 
                          WHERE sc.delegation_id = dlg.id 
                            AND sc.scope_type = 'SERVICE' 
                            AND sc.scope_value = s.service_code
                      )
                  )
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
          AND EXISTS (
                SELECT 1 
                FROM sys_user_delegations dlg
                WHERE dlg.from_user_id = d.current_handler_id
                  AND dlg.to_user_id = :leaderId
                  AND NOW() BETWEEN dlg.start_time AND dlg.end_time
                  AND (
                      NOT EXISTS (SELECT 1 FROM sys_delegation_scopes sc WHERE sc.delegation_id = dlg.id)
                      OR 
                      EXISTS (
                          SELECT 1 FROM sys_delegation_scopes sc 
                          WHERE sc.delegation_id = dlg.id 
                            AND sc.scope_type = 'DOMAIN' 
                            AND sc.scope_value = s.domain
                      )
                      OR
                      EXISTS (
                          SELECT 1 FROM sys_delegation_scopes sc 
                          WHERE sc.delegation_id = dlg.id 
                            AND sc.scope_type = 'SERVICE' 
                            AND sc.scope_value = s.service_code
                      )
                  )
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

    long countByCurrentHandler_IdAndDossierStatus(Long currentHandlerId, String dossierStatus);

    /**
     * Đếm số lượng hồ sơ cần xử lý dựa trên sự ủy quyền.
     * @param delegateeId ID của người được ủy quyền (trong câu SQL của bạn là 3)
     * @param status Trạng thái hồ sơ (ví dụ: 'VERIFIED')
     */
    @Query(value = """
        SELECT COUNT(d.id) 
        FROM ops_dossiers d
        JOIN cat_services s ON d.service_id = s.id
        JOIN sys_user_delegations del ON d.current_handler_id = del.from_user_id
        WHERE del.to_user_id = :delegateeId
          AND NOW() BETWEEN del.start_time AND del.end_time
          AND d.dossier_status = :status
          AND (
              -- Trường hợp 1: Không có scope nào (Ủy quyền toàn bộ)
              NOT EXISTS (SELECT 1 FROM sys_delegation_scopes sc WHERE sc.delegation_id = del.id)
              
              OR 
              
              -- Trường hợp 2: Có scope khớp với DOMAIN
              EXISTS (
                  SELECT 1 FROM sys_delegation_scopes sc 
                  WHERE sc.delegation_id = del.id 
                    AND sc.scope_type = 'DOMAIN' 
                    AND sc.scope_value = s.domain
              )
              
              OR
              
              -- Trường hợp 3: Có scope khớp với SERVICE_CODE
              EXISTS (
                  SELECT 1 FROM sys_delegation_scopes sc 
                  WHERE sc.delegation_id = del.id 
                    AND sc.scope_type = 'SERVICE' 
                    AND sc.scope_value = s.service_code
              )
          )
        """, nativeQuery = true)
    long countDelegatedDossiers(@Param("delegateeId") Long delegateeId,
                                @Param("status") String status);


    @Query(value = """
        SELECT 
            ROUND(SUM(CASE WHEN dos.finish_date <= dos.due_date THEN 1 ELSE 0 END) * 100.0 / COUNT(dos.id), 2)
        FROM ops_dossiers dos
        JOIN sys_departments dpt ON dos.receiving_dept_id = dpt.id
        WHERE dpt.id = :deptId
          AND MONTH(dos.finish_date) = MONTH(CURRENT_DATE())
          AND YEAR(dos.finish_date) = YEAR(CURRENT_DATE())
          AND dos.dossier_status IN ('APPROVED', 'REJECTED')
        """, nativeQuery = true)
    Double getOnTimeRateByDeptId(@Param("deptId") Long deptId);


    @Query("""
    SELECT COUNT(dos) 
    FROM OpsDossier dos 
    WHERE dos.receivingDept.id = :deptId
    """)
    long countAllDossiersByDept(@Param("deptId") Long deptId);
    @Query("""
    SELECT COUNT(dos) 
    FROM OpsDossier dos 
    WHERE dos.receivingDept.id = :deptId
      AND dos.dossierStatus NOT IN ('APPROVED', 'REJECTED')
      AND dos.dueDate < CURRENT_TIMESTAMP
    """)
    long countOverdueDossiersByDept(@Param("deptId") Long deptId);

    @Query("""
    SELECT COALESCE(AVG(f.rating), 0.0)
    FROM ModFeedback f
    WHERE f.dossierId IN (
        SELECT d.id FROM OpsDossier d WHERE d.receivingDept.id = :deptId
    )
    """)
    Double getAverageSatisfactionScoreByDept(@Param("deptId") Long deptId);
}
