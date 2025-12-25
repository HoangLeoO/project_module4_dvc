package org.example.project_module4_dvc.service.sys;

import org.example.project_module4_dvc.dto.admin.AdminUserListDTO;
import org.example.project_module4_dvc.entity.sys.SysUser;
import org.example.project_module4_dvc.repository.sys.SysUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SysUserService implements ISysUserService {

    @Autowired
    private SysUserRepository userRepository;

    @Override
    public SysUser findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public SysUser findById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Autowired
    private org.example.project_module4_dvc.repository.mock.MockCitizenRepository citizenRepository;

    @Autowired
    private org.example.project_module4_dvc.repository.sys.SysDepartmentRepository departmentRepository;

    @Autowired
    private org.example.project_module4_dvc.repository.sys.SysRoleRepository roleRepository;

    @Autowired
    private org.example.project_module4_dvc.repository.sys.SysUserRoleRepository userRoleRepository;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Override
    public List<AdminUserListDTO> getOfficials() {

        List<Object[]> rows = userRepository.findOfficialsRaw();

        Map<Long, AdminUserListDTO> map = new LinkedHashMap<>();

        for (Object[] r : rows) {
            Long id = (Long) r[0];

            map.computeIfAbsent(id, k -> {
                AdminUserListDTO dto = new AdminUserListDTO();
                dto.setId(id);
                dto.setUsername((String) r[1]);
                dto.setFullName((String) r[2]);
                dto.setDepartmentName((String) r[3]);
                dto.setRoles(new ArrayList<>());

                // Set new fields
                dto.setDeptId((Long) r[5]);
                dto.setRoleIds(new ArrayList<>());
                dto.setIsActive((Boolean) r[7]);

                return dto;
            });

            if (r[4] != null) {
                map.get(id).getRoles().add((String) r[4]);
            }
            if (r[6] != null) {
                map.get(id).getRoleIds().add((Long) r[6]);
            }
        }

        return new ArrayList<>(map.values());
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void createOfficial(org.example.project_module4_dvc.dto.admin.AdminCreateUserDTO dto) {
        // 1. Validate Citizen
        org.example.project_module4_dvc.entity.mock.MockCitizen citizen = citizenRepository.findById(dto.getCitizenId())
                .orElseThrow(() -> new RuntimeException("Công dân không tồn tại"));

        if (userRepository.existsByCitizenIdAndUserType(citizen.getId(), "OFFICIAL")) {
            throw new RuntimeException("Công dân này đã có tài khoản cán bộ");
        }

        // 2. Validate Username
        if (userRepository.findByUsername(dto.getUsername()) != null) {
            throw new RuntimeException("Tên đăng nhập đã tồn tại");
        }

        // 3. Create SysUser
        SysUser user = new SysUser();
        user.setUsername(dto.getUsername());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));

        // Use full name from Citizen if not provided
        String fullName = (dto.getFullName() != null && !dto.getFullName().isEmpty()) ? dto.getFullName()
                : citizen.getFullName();
        user.setFullName(fullName);

        user.setUserType("OFFICIAL");
        user.setCitizen(citizen);

        // 4. Validate & Set Department
        if (dto.getDeptId() == null) {
            throw new RuntimeException("Vui lòng chọn Đơn vị hành chính");
        }
        org.example.project_module4_dvc.entity.sys.SysDepartment dept = departmentRepository
                .findById(dto.getDeptId())
                .orElseThrow(() -> new RuntimeException("Đơn vị không tồn tại"));
        user.setDepartment(dept);

        userRepository.save(user); // Save first to get ID

        // 5. Validate & Set Roles
        if (dto.getRoleIds() == null || dto.getRoleIds().isEmpty()) {
            throw new RuntimeException("Vui lòng chọn ít nhất 1 vai trò");
        }

        for (Long roleId : dto.getRoleIds()) {
            org.example.project_module4_dvc.entity.sys.SysRole role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new RuntimeException("Role không tồn tại: " + roleId));

            org.example.project_module4_dvc.entity.sys.SysUserRole userRole = new org.example.project_module4_dvc.entity.sys.SysUserRole();
            userRole.setUser(user);
            userRole.setRole(role);
            userRoleRepository.save(userRole);
        }
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void updateOfficial(org.example.project_module4_dvc.dto.admin.AdminUpdateUserDTO dto) {
        SysUser user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        // 1. Update Department
        if (dto.getDeptId() != null) {
            org.example.project_module4_dvc.entity.sys.SysDepartment dept = departmentRepository
                    .findById(dto.getDeptId())
                    .orElseThrow(() -> new RuntimeException("Đơn vị không tồn tại"));
            user.setDepartment(dept);
        } else {
            user.setDepartment(null);
        }

        // 2. Update Roles
        // Clear existing
        userRoleRepository.deleteByUserId(user.getId());

        // Add new
        if (dto.getRoleIds() != null && !dto.getRoleIds().isEmpty()) {
            for (Long roleId : dto.getRoleIds()) {
                org.example.project_module4_dvc.entity.sys.SysRole role = roleRepository.findById(roleId)
                        .orElseThrow(() -> new RuntimeException("Role không tồn tại: " + roleId));

                org.example.project_module4_dvc.entity.sys.SysUserRole userRole = new org.example.project_module4_dvc.entity.sys.SysUserRole();
                userRole.setUser(user);
                userRole.setRole(role);
                userRoleRepository.save(userRole);
            }
        }

        userRepository.save(user);
    }

    @Override
    public void toggleStatus(Long userId) {
        SysUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        // Toggle
        boolean current = user.getIsActive() != null ? user.getIsActive() : true;
        user.setIsActive(!current);

        userRepository.save(user);
    }
}
