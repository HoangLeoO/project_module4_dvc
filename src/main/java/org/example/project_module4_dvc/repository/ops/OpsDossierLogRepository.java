package org.example.project_module4_dvc.repository.ops;

import org.example.project_module4_dvc.dto.timeline.IDossierLogProjectionDTO;
import org.example.project_module4_dvc.entity.ops.OpsDossierLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OpsDossierLogRepository extends JpaRepository<OpsDossierLog, Long> {

    @Query(value = """
                SELECT
                    l.created_at as createdAt,
                    l.comments as comments,
                    u.full_name as actorName,
                    d.dept_name as deptName,
                    l.action as action
                FROM ops_dossier_logs l
                JOIN sys_users u ON l.actor_id = u.id
                LEFT JOIN sys_departments d ON u.dept_id = d.id
                WHERE l.dossier_id = :dossierId
                ORDER BY l.created_at ASC
            """, nativeQuery = true)
    List<IDossierLogProjectionDTO> findLogsByDossierId(@Param("dossierId") Long dossierId);
}