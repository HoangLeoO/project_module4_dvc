package org.example.project_module4_dvc.service.sys;

import lombok.RequiredArgsConstructor;
import org.example.project_module4_dvc.entity.sys.SysUserRole;
import org.example.project_module4_dvc.repository.sys.SysUserRoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class SysUserRoleService implements ISysUserRoleService{
    private final SysUserRoleRepository sysUserRoleRepository;

    @Override
    public List<SysUserRole> findAll() {
        return sysUserRoleRepository.findAll();
    }
}
