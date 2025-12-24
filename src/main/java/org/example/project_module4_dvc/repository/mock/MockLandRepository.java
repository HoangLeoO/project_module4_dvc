package org.example.project_module4_dvc.repository.mock;

import org.example.project_module4_dvc.entity.mock.MockLand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MockLandRepository extends JpaRepository<MockLand, Long> {
    List<MockLand> findByOwnerId(Long ownerId);
}