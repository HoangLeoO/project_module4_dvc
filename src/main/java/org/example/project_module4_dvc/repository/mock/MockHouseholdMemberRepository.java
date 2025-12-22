package org.example.project_module4_dvc.repository.mock;

import org.example.project_module4_dvc.entity.mock.MockHouseholdMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MockHouseholdMemberRepository extends JpaRepository<MockHouseholdMember, Long> {
    java.util.List<MockHouseholdMember> findByCitizen_IdAndStatus(Long citizenId, Integer status);
    java.util.List<MockHouseholdMember> findByCitizen_CccdAndStatus(String cccd, Integer status);
}