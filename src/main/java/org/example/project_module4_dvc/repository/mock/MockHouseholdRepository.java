package org.example.project_module4_dvc.repository.mock;

import org.example.project_module4_dvc.entity.mock.MockHousehold;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MockHouseholdRepository extends JpaRepository<MockHousehold, Long> {
}
