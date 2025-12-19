package org.example.project_module4_dvc.service;

import org.example.project_module4_dvc.entity.sys.SysUser;
import org.example.project_module4_dvc.repository.sys.SysUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysUserService implements ISysUserService{

    @Autowired
    private SysUserRepository userRepository;

    @Override
    public SysUser findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
