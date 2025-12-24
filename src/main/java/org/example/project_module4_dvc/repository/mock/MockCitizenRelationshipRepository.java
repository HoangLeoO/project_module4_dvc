package org.example.project_module4_dvc.repository.mock;

import org.example.project_module4_dvc.entity.mock.MockCitizenRelationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MockCitizenRelationshipRepository extends JpaRepository<MockCitizenRelationship, Long> {
    List<MockCitizenRelationship> findByCitizenId(Long citizenId);
    List<MockCitizenRelationship> findByRelativeId(Long relativeId);
}
