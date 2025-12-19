package org.example.project_module4_dvc.service;

import jakarta.transaction.Transactional;
import org.example.project_module4_dvc.entity.sys.SysUser;
import org.example.project_module4_dvc.entity.sys.SysUserRole;
import org.example.project_module4_dvc.repository.sys.SysUserRepository;
import org.example.project_module4_dvc.repository.sys.SysUserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private SysUserRepository sysUserRepository;

    @Autowired
    private SysUserRoleRepository sysUserRoleRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        SysUser user = sysUserRepository.findByUsername(username);
        if (user == null){
            System.out.println("User not found " + username );
            throw new UsernameNotFoundException("User " + username + " was not found in the database");
        }
        System.out.println("Found User: " + user);
        List<SysUserRole> userRoles = sysUserRoleRepository.findByUser(user);

        List<GrantedAuthority> grantList = new ArrayList<GrantedAuthority>();

        if (userRoles != null) {
            for (SysUserRole userRole : userRoles) {
                // ROLE_USER, ROLE_ADMIN,..
                GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + userRole.getRole().getRoleName());
                grantList.add(authority);
            }
        }
        String deptName = (user.getDepartment() != null) ? user.getDepartment().getDeptName() : "UBND TP. Đà Nẵng";
        
        UserDetails userDetails = new org.example.project_module4_dvc.config.CustomUserDetails(
                user.getUsername(),
                user.getPasswordHash(),
                grantList,
                user.getFullName(),
                deptName
        );

        return userDetails;
    }
}
