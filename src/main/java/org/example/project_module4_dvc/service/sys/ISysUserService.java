package org.example.project_module4_dvc.service.sys;

import org.example.project_module4_dvc.entity.sys.SysUser;

public interface ISysUserService {
    SysUser findByUsername(String username);
}
