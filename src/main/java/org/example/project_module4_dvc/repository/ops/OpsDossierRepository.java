package org.example.project_module4_dvc.repository.ops;

import org.example.project_module4_dvc.entity.ops.OpsDossier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OpsDossierRepository extends JpaRepository<OpsDossier, Long> {

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

    // Quá hạn
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

    @Query("SELECT d FROM OpsDossier d WHERE d.dossierStatus NOT IN ('APPROVED','REJECTED') AND d.dueDate < CURRENT_TIMESTAMP")
    List<OpsDossier> findOverdueDossiers();

    List<OpsDossier> findByDossierStatusNotIn(List<String> statusList);
}
