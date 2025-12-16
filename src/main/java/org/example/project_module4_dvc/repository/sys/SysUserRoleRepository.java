package org.example.project_module4_dvc.repository.sys;

import org.example.project_module4_dvc.entity.sys.SysUserRole;
import org.example.project_module4_dvc.entity.sys.SysUserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SysUserRoleRepository extends JpaRepository<SysUserRole, SysUserRoleId> {
}