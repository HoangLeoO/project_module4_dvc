package org.example.project_module4_dvc.repository.sys;

import org.example.project_module4_dvc.entity.sys.SysUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SysUserRepository extends JpaRepository<SysUser, Long> {
    SysUser findByUsername(String username);

    @Query("SELECT DISTINCT u FROM SysUser u " +
            "JOIN SysUserRole ur ON u.id = ur.user.id " +
            "JOIN SysRole r ON ur.role.id = r.id " +
            "WHERE u.department.id = :deptId " +
            "AND u.id != :excludeUserId " +
            "AND r.roleName IN :roleNames")
    List<SysUser> findPotentialDelegatees(Long deptId, Long excludeUserId, java.util.List<String> roleNames);
}