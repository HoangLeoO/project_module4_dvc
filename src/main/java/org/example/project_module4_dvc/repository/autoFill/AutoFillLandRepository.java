package org.example.project_module4_dvc.repository.autoFill;

import org.example.project_module4_dvc.entity.mock.MockLand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AutoFillLandRepository extends JpaRepository<MockLand, Long> {
    List<MockLand> findAllByOwner_Cccd(String cccd);
}
