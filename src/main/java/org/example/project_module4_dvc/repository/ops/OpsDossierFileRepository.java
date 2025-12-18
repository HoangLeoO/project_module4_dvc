package org.example.project_module4_dvc.repository.ops;

import org.example.project_module4_dvc.entity.ops.OpsDossierFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OpsDossierFileRepository extends JpaRepository<OpsDossierFile, Long> {

    List<OpsDossierFile> findOpsDossierFileByDossier_Id(Long dossierId);
}