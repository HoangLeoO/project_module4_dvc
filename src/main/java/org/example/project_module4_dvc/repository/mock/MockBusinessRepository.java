package org.example.project_module4_dvc.repository.mock;

import org.example.project_module4_dvc.entity.mock.MockBusiness;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MockBusinessRepository extends JpaRepository<MockBusiness, Long> {
    @org.springframework.data.jpa.repository.Query("SELECT MAX(b.taxCode) FROM MockBusiness b")
    String findMaxTaxCode();
}