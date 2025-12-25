package org.example.project_module4_dvc.repository.sys;

import org.example.project_module4_dvc.entity.sys.SysRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SysRoleRepository extends JpaRepository<SysRole, Long> {
    SysRole findByRoleName(String roleName);
}
