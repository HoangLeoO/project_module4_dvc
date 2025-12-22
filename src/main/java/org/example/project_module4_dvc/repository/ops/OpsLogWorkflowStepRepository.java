package org.example.project_module4_dvc.repository.ops;

import org.example.project_module4_dvc.entity.ops.OpsLogWorkflowStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OpsLogWorkflowStepRepository extends JpaRepository<OpsLogWorkflowStep, Long> {

    /**
     * Lấy step_order cao nhất mà hồ sơ đã đi qua
     * Dựa vào bảng ops_log_workflow_steps để biết chính xác bước nào đã hoàn thành
     */
    @Query(value = """
            SELECT MAX(s.step_order)
            FROM ops_log_workflow_steps lws
            JOIN cat_workflow_steps s ON lws.workflow_step_id = s.id
            JOIN ops_dossier_logs l ON lws.log_id = l.id
            WHERE l.dossier_id = :dossierId
            """, nativeQuery = true)
    Integer findMaxStepOrderByDossierId(@Param("dossierId") Long dossierId);
}
