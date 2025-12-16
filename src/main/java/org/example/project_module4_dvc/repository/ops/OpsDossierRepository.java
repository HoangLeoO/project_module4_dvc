package org.example.project_module4_dvc.repository.ops;

import org.example.project_module4_dvc.entity.ops.OpsDossier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OpsDossierRepository extends JpaRepository<OpsDossier, Long> {
}