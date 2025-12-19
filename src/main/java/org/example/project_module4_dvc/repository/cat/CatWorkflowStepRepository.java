package org.example.project_module4_dvc.repository.cat;

import org.example.project_module4_dvc.dto.timeline.IWorkflowStepProjectionDTO;
import org.example.project_module4_dvc.entity.cat.CatWorkflowStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CatWorkflowStepRepository extends JpaRepository<CatWorkflowStep, Long> {

    @Query("""
                SELECT ws.stepOrder as stepOrder, ws.stepName as stepName
                FROM CatWorkflowStep ws
                WHERE ws.service.id = (SELECT d.service.id FROM OpsDossier d WHERE d.id = :dossierId)
                ORDER BY ws.stepOrder ASC
            """)
    List<IWorkflowStepProjectionDTO> findWorkflowSteps(@Param("dossierId") Long dossierId);

    @Query("""
                SELECT ws.stepOrder as stepOrder, ws.stepName as stepName
                FROM CatWorkflowStep ws
                WHERE ws.service.id = :serviceId
                ORDER BY ws.stepOrder ASC
            """)
    List<IWorkflowStepProjectionDTO> findByServiceIdOrderByStepOrderAsc(@Param("serviceId") Long serviceId);
}