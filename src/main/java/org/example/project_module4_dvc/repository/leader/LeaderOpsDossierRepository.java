package org.example.project_module4_dvc.repository.leader;

import jakarta.transaction.Transactional;
import org.example.project_module4_dvc.dto.leader.DossierApprovalSummaryDTO;
import org.example.project_module4_dvc.dto.leader.report.ReportDomainStatDTO;
import org.example.project_module4_dvc.entity.ops.OpsDossier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LeaderOpsDossierRepository extends JpaRepository<OpsDossier, Long> {

  @Query("""
          SELECT new org.example.project_module4_dvc.dto.leader.DossierApprovalSummaryDTO(
              d.id, d.dossierCode, d.applicant.fullName, d.service.serviceName,
              d.service.domain, d.receivingDept.deptName, d.currentHandler.fullName,
              d.dossierStatus, d.finishDate,
              CAST(function('DATEDIFF', d.dueDate, CURRENT_DATE) AS long),
              d.formData
          )
          FROM OpsDossier d
          WHERE d.dossierStatus = 'VERIFIED'
            AND d.currentHandler.id = :leaderId
            AND (:applicantName IS NULL OR d.applicant.fullName LIKE CONCAT('%', :applicantName, '%'))
            AND (:domain IS NULL OR d.service.domain = :domain)
      """)
  Page<DossierApprovalSummaryDTO> findMyPendingDossiers(@Param("leaderId") Long leaderId,
      @Param("applicantName") String applicantName,
      @Param("domain") String domain,
      Pageable pageable);

  @Query("""
          SELECT new org.example.project_module4_dvc.dto.leader.DossierApprovalSummaryDTO(
              d.id, d.dossierCode, d.applicant.fullName, d.service.serviceName,
              d.service.domain, d.receivingDept.deptName, d.currentHandler.fullName,
              d.dossierStatus, d.finishDate,
              CAST(function('DATEDIFF', d.dueDate, CURRENT_DATE) AS long),
              d.formData
          )
          FROM OpsDossier d
          WHERE d.dossierStatus = 'VERIFIED'
            AND (:applicantName IS NULL OR d.applicant.fullName LIKE CONCAT('%', :applicantName, '%'))
            AND (:domain IS NULL OR d.service.domain = :domain)
            AND EXISTS (
               SELECT dlg FROM SysUserDelegation dlg
               WHERE dlg.fromUser.id = d.currentHandler.id
                 AND dlg.toUser.id = :leaderId
                 AND CURRENT_TIMESTAMP BETWEEN dlg.startTime AND dlg.endTime
                 AND (
                     dlg.delegationScopes IS EMPTY
                     OR EXISTS (SELECT s FROM dlg.delegationScopes s WHERE s.scopeType = 'DOMAIN' AND s.scopeValue = d.service.domain)
                     OR EXISTS (SELECT s FROM dlg.delegationScopes s WHERE s.scopeType = 'SERVICE' AND s.scopeValue = d.service.serviceCode)
                 )
            )
      """)
  Page<DossierApprovalSummaryDTO> findDelegatedPendingDossiers(@Param("leaderId") Long leaderId,
      @Param("applicantName") String applicantName,
      @Param("domain") String domain,
      Pageable pageable);

  @Query("""
          SELECT new org.example.project_module4_dvc.dto.leader.DossierApprovalSummaryDTO(
              d.id, d.dossierCode, d.applicant.fullName, d.service.serviceName,
              d.service.domain, d.receivingDept.deptName, d.currentHandler.fullName,
              d.dossierStatus, d.finishDate, 0L,
              d.formData
          )
          FROM OpsDossier d
          WHERE d.dossierStatus IN ('APPROVED', 'RESULT_RETURNED')
            AND (:applicantName IS NULL OR d.applicant.fullName LIKE CONCAT('%', :applicantName, '%'))
            AND (:domain IS NULL OR d.service.domain = :domain)
            AND (
                d.currentHandler.id = :leaderId
                OR EXISTS (
                   SELECT dlg FROM SysUserDelegation dlg
                   WHERE dlg.fromUser.id = :leaderId
                     AND dlg.toUser.id = d.currentHandler.id
                     AND d.finishDate BETWEEN dlg.startTime AND dlg.endTime
                )
            )
          ORDER BY d.finishDate DESC
      """)
  Page<DossierApprovalSummaryDTO> findApprovedHistory(@Param("leaderId") Long leaderId,
      @Param("applicantName") String applicantName,
      @Param("domain") String domain,
      Pageable pageable);

  @Transactional
  @Modifying
  @Query("UPDATE OpsDossier d SET d.dossierStatus = 'APPROVED', d.finishDate = CURRENT_TIMESTAMP, d.currentHandler.id = :leaderId WHERE d.id = :dossierId")
  void updateStatusApprovedDossier(@Param("leaderId") Long leaderId, @Param("dossierId") Long dossierId);

  long countByCurrentHandler_IdAndDossierStatus(Long currentHandlerId, String dossierStatus);

  @Query("""
          SELECT COUNT(d)
          FROM OpsDossier d
          WHERE d.dossierStatus = :status
            AND EXISTS (
               SELECT dlg FROM SysUserDelegation dlg
               WHERE dlg.fromUser.id = d.currentHandler.id
                 AND dlg.toUser.id = :delegateeId
                 AND CURRENT_TIMESTAMP BETWEEN dlg.startTime AND dlg.endTime
                 AND (
                     dlg.delegationScopes IS EMPTY
                     OR EXISTS (SELECT s FROM dlg.delegationScopes s WHERE s.scopeType = 'DOMAIN' AND s.scopeValue = d.service.domain)
                     OR EXISTS (SELECT s FROM dlg.delegationScopes s WHERE s.scopeType = 'SERVICE' AND s.scopeValue = d.service.serviceCode)
                 )
            )
      """)
  long countDelegatedDossiers(@Param("delegateeId") Long delegateeId, @Param("status") String status);

  @Query(value = """
      SELECT ROUND(SUM(CASE WHEN dos.finish_date <= dos.due_date THEN 1 ELSE 0 END) * 100.0 / COUNT(dos.id), 2)
      FROM ops_dossiers dos
      WHERE dos.receiving_dept_id = :deptId
        AND MONTH(dos.finish_date) = MONTH(CURRENT_DATE())
        AND YEAR(dos.finish_date) = YEAR(CURRENT_DATE())
        AND dos.dossier_status IN ('APPROVED', 'REJECTED')
      """, nativeQuery = true)
  Double getOnTimeRateByDeptId(@Param("deptId") Long deptId);

  @Query("SELECT COUNT(d) FROM OpsDossier d WHERE d.receivingDept.id = :deptId")
  long countAllDossiersByDept(@Param("deptId") Long deptId);

  @Query("SELECT COUNT(d) FROM OpsDossier d WHERE d.receivingDept.id = :deptId AND d.dossierStatus NOT IN ('APPROVED', 'REJECTED') AND d.dueDate < CURRENT_TIMESTAMP")
  long countOverdueDossiersByDept(@Param("deptId") Long deptId);

  @Query("SELECT COALESCE(AVG(f.rating), 0.0) FROM ModFeedback f JOIN OpsDossier d ON f.dossierId = d.id WHERE d.receivingDept.id = :deptId")
  Double getAverageSatisfactionScoreByDept(@Param("deptId") Long deptId);

  // --- REPORTING QUERIES ---

  @Query("SELECT COUNT(d) FROM OpsDossier d WHERE d.receivingDept.id = :deptId AND d.submissionDate BETWEEN :startDate AND :endDate")
  long countReceivedInPeriod(@Param("deptId") Long deptId, @Param("startDate") LocalDateTime start,
      @Param("endDate") LocalDateTime end);

  @Query("SELECT COUNT(d) FROM OpsDossier d WHERE d.receivingDept.id = :deptId AND d.dossierStatus IN ('APPROVED', 'REJECTED') AND d.finishDate BETWEEN :startDate AND :endDate")
  long countResolvedInPeriod(@Param("deptId") Long deptId, @Param("startDate") LocalDateTime start,
      @Param("endDate") LocalDateTime end);

  @Query("SELECT COUNT(d) FROM OpsDossier d WHERE d.receivingDept.id = :deptId AND d.dossierStatus IN ('APPROVED', 'REJECTED') AND d.finishDate <= d.dueDate AND d.finishDate BETWEEN :startDate AND :endDate")
  long countOnTimeInPeriod(@Param("deptId") Long deptId, @Param("startDate") LocalDateTime start,
      @Param("endDate") LocalDateTime end);

  @Query("SELECT COUNT(d) FROM OpsDossier d WHERE d.receivingDept.id = :deptId AND d.dossierStatus IN ('REJECTED') AND d.finishDate BETWEEN :startDate AND :endDate")
  long countRejectedInPeriod(@Param("deptId") Long deptId, @Param("startDate") LocalDateTime start,
      @Param("endDate") LocalDateTime end);

  @Query("""
          SELECT new org.example.project_module4_dvc.dto.leader.report.ReportDomainStatDTO(
                 d.service.domain,
                 COUNT(d),
                 SUM(CASE WHEN d.dossierStatus IN ('NEW', 'RECEIVED', 'PROCESSING', 'VERIFIED') THEN 1L ELSE 0L END),
                 SUM(CASE WHEN d.dossierStatus IN ('APPROVED', 'REJECTED') THEN 1L ELSE 0L END),
                 SUM(CASE WHEN d.dossierStatus IN ('APPROVED', 'REJECTED') AND d.finishDate <= d.dueDate THEN 1L ELSE 0L END)
          )
          FROM OpsDossier d
          WHERE d.receivingDept.id = :deptId AND d.submissionDate BETWEEN :startDate AND :endDate
          GROUP BY d.service.domain
      """)
  List<ReportDomainStatDTO> getStatsByDomain(
      @Param("deptId") Long deptId,
      @Param("startDate") LocalDateTime start,
      @Param("endDate") LocalDateTime end);
}
