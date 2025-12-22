package org.example.project_module4_dvc.config;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class CustomUserDetails extends User {
    private final Long userId;
    private final Long citizenId;
    private final String fullName;
    private final String departmentName;



    public CustomUserDetails(Long userId, String username, String password,
                             Collection<? extends GrantedAuthority> authorities, String fullName, String departmentName,
                             Long citizenId) {
        super(username, password, authorities);
        this.userId = userId;
        this.fullName = fullName;
        this.departmentName = departmentName;
        this.citizenId = citizenId;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getCitizenId() {
        return citizenId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getDepartmentName() {
        return departmentName;
    }


}
