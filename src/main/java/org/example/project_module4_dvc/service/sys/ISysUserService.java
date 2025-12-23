package org.example.project_module4_dvc.service.sys;

import org.example.project_module4_dvc.dto.admin.AdminUserListDTO;
import org.example.project_module4_dvc.entity.sys.SysUser;

import java.util.List;
import java.util.Optional;

public interface ISysUserService {
    SysUser findByUsername(String username);

    SysUser findById(Long userId);

    List<AdminUserListDTO> getOfficials();
}
