package org.example.project_module4_dvc.repository.cat;
import org.example.project_module4_dvc.entity.cat.CatTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface CatTemplateRepository extends JpaRepository<CatTemplate, Long> {
}