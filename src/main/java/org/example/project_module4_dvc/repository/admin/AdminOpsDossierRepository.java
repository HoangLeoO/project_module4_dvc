package org.example.project_module4_dvc.repository.admin;

import org.example.project_module4_dvc.entity.ops.OpsDossier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AdminOpsDossierRepository  extends JpaRepository<OpsDossier, Long> {
    @Query("""
    SELECT d FROM OpsDossier d
    WHERE d.dueDate BETWEEN :from AND :to
      AND d.dossierStatus <> 'DONE'
""")
    List<OpsDossier> findNearlyDue(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );
}
