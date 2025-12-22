package org.example.project_module4_dvc.repository.ops;

import org.example.project_module4_dvc.entity.ops.OpsDossier;
import org.example.project_module4_dvc.entity.ops.OpsDossierResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OpsDossierResultRepository extends JpaRepository<OpsDossierResult, Long> {
    Page<OpsDossierResult> findOpsDossierResultByDossier_ReceivingDept_DeptName(String departmentName, Pageable pageable);

}