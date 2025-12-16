package org.example.project_module4_dvc.repository.cat;

import org.example.project_module4_dvc.entity.cat.CatKnowledgeBase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CatKnowledgeBaseRepository extends JpaRepository<CatKnowledgeBase, Long> {
}