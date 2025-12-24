package org.example.project_module4_dvc.repository.mock;

import org.example.project_module4_dvc.entity.mock.MockCitizenRelationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MockCitizenRelationshipRepository extends JpaRepository<MockCitizenRelationship, Long> {

    /**
     * Tìm tất cả các mối quan hệ của một công dân (chỉ chiều citizen -> relative)
     * VD: Nếu A là cha của B, query với A sẽ trả về relationship 
     * CON" (A có con là B)
     * 
     * @param citizenId ID của công dân
     * @return Danh sách các mối quan hệ
     */
    @Query("SELECT r FROM MockCitizenRelationship r " +
            "JOIN FETCH r.citizen " +
            "JOIN FETCH r.relative " +
            "WHERE r.citizen.id = :citizenId")
    List<MockCitizenRelationship> findByCitizenId(@Param("citizenId") Long citizenId);

    /**
     * Tìm tất cả các mối quan hệ ngược (relative -> citizen)
     * 
     * VD: Nếu B có cha        là A (B, A, 'CHA'), query với A sẽ trả về relationship này
     * @param relativeId ID của người thân
     * @return Danh sách các mối quan hệ ngược
     */
    @Query("SELECT r FROM MockCitizenRelationship r " +
            "JOIN FETCH r.citizen " +
            "JOIN FETCH r.relative " +
            "WHERE r.relative.id = :relativeId")
    List<MockCitizenRelationship> findByRelativeId(@Param("relativeId") Long relativeId);

    /**
     * Tìm mối quan hệ theo loại (VD: CHA, ME, CON, VO, CHONG)
     * @param citizenId ID của công dân
     * @param relationshipType Loại mối quan hệ
     * @return Danh sách các mối quan hệ
     */
    @Query("SELECT r FROM MockCitizenRelationship r " +
           "JOIN FETCH r.citizen " +
           "JOIN FETCH r.relative " +
           "WHERE r.citizen.id = :citizenId AND r.relationshipType = :relationshipType")
    List<MockCitizenRelationship> findByCitizenIdAndRelationshipType(
            @Param("citizenId") Long citizenId,
            @Param("relationshipType") String relationshipType);
}
