package org.example.project_module4_dvc.repository.sys;

import org.example.project_module4_dvc.entity.sys.SysUserDelegation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SysUserDelegationRepository extends JpaRepository<SysUserDelegation, Long> {
}