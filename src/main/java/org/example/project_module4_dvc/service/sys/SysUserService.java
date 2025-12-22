package org.example.project_module4_dvc.service.sys;

import org.example.project_module4_dvc.entity.sys.SysUser;
import org.example.project_module4_dvc.repository.sys.SysUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
        return  userRepository.findById(userId).orElse(null);
    }
}
