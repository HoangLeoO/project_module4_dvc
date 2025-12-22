package org.example.project_module4_dvc.repository.autoFill;

import org.example.project_module4_dvc.entity.mock.MockCitizen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AutoFillCitizenRepository extends JpaRepository<MockCitizen,Long> {
    Optional<MockCitizen> findByCccd(String cccd);
    Optional<MockCitizen> findById(Long id);
}
