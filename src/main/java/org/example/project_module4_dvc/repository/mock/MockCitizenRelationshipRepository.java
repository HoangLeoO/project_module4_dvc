package org.example.project_module4_dvc.repository.mock;

import org.example.project_module4_dvc.entity.mock.MockCitizenRelationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MockCitizenRelationshipRepository extends JpaRepository<MockCitizenRelationship, Long> {

    /**
     * Tìm tất cả các mối quan hệ của một công dân
     * 
     * @param citizenId ID của công dân
     * @return Danh sách các mối quan hệ
     */
    List<MockCitizenRelationship> findByCitizenId(Long citizenId);

    /**
     * Tìm mối quan hệ theo loại (VD: CHA, ME, CON, VO, CHONG)
     * 
     * @param citizenId        ID của công dân
     * @param relationshipType Loại mối quan hệ
     * @return Danh sách các mối quan hệ
     */
    List<MockCitizenRelationship> findByCitizenIdAndRelationshipType(Long citizenId, String relationshipType);
}
