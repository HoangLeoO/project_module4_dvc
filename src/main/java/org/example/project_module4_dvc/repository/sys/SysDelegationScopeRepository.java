package org.example.project_module4_dvc.repository.sys;

import org.example.project_module4_dvc.entity.sys.SysDelegationScope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SysDelegationScopeRepository extends JpaRepository<SysDelegationScope, Long> {
    List<SysDelegationScope> findByDelegation_Id(Long delegationId);
}
