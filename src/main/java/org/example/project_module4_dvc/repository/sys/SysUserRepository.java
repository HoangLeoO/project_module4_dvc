package org.example.project_module4_dvc.repository.sys;

import org.example.project_module4_dvc.dto.specialist.SpecialistAvailableDTO;
import org.example.project_module4_dvc.entity.sys.SysUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SysUserRepository extends JpaRepository<SysUser, Long> {
    SysUser findByUsername(String username);

    boolean existsByCitizenIdAndUserType(Long citizenId, String userType);

    @Query("SELECT DISTINCT u FROM SysUser u " +
            "JOIN SysUserRole ur ON u.id = ur.user.id " +
            "JOIN SysRole r ON ur.role.id = r.id " +
            "WHERE u.department.id = :deptId " +
            "AND u.id != :excludeUserId " +
            "AND r.roleName IN :roleNames")
    List<SysUser> findPotentialDelegatees(Long deptId, Long excludeUserId, java.util.List<String> roleNames);

    /**
     * Lấy danh sách specialist có thể xử lý hồ sơ
     * Dựa trên role_id được định nghĩa trong cat_services
     */
    /**
     * Lấy danh sách specialist entities có thể xử lý hồ sơ
     * Kết quả cần map thủ công sang DTO trong Service layer
     */
    @Query("""
            SELECT new org.example.project_module4_dvc.dto.specialist. SpecialistAvailableDTO(
                u.id,
                u.fullName,
                u.username,
                dept.deptName,
                r.roleName,
                COUNT(activeDossier.id)
            )
            FROM SysUser u
            JOIN u.department dept
            JOIN SysUserRole ur ON ur.user.id = u.id
            JOIN ur.role r
            CROSS JOIN OpsDossier targetDossier
            JOIN targetDossier.service s
            LEFT JOIN OpsDossier activeDossier
                ON activeDossier. currentHandler.id = u.id
                AND activeDossier. dossierStatus IN ('PENDING', 'VERIFIED')
            WHERE targetDossier.id = :dossierId
              AND u.userType = 'OFFICIAL'
              AND u.department.id = targetDossier. receivingDept.id
              AND r.id = s.sysRole.id
            GROUP BY u.id, u.fullName, u.username, dept. deptName, r.roleName
            ORDER BY COUNT(activeDossier.id) ASC
            """)
    List<SpecialistAvailableDTO> findAvailableSpecialistsForDossier(@Param("dossierId") Long dossierId);

    // Raw query để xuất báo cáo danh sách cán bộ
    @Query("""
                SELECT u.id,
                       u.username,
                       u.fullName,
                       d.deptName,
                       r.roleName,
                       d.id,
                       r.id,
                       u.isActive
                FROM SysUser u
                LEFT JOIN u.department d
                LEFT JOIN SysUserRole ur ON ur.user = u
                LEFT JOIN ur.role r
                WHERE u.userType = 'OFFICIAL'
            """)
    List<Object[]> findOfficialsRaw();

}