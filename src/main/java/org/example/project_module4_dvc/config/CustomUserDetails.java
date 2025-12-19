//package org.example.project_module4_dvc.config;
//
//import lombok.Getter;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.userdetails.User;
//
//import java.util.Collection;
//
//public class CustomUserDetails extends User {
//    private final String fullName;
//    private final String departmentName;
//
//    public CustomUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities, String fullName, String departmentName) {
//        super(username, password, authorities);
//        this.fullName = fullName;
//        this.departmentName = departmentName;
//    }
//
//    public String getFullName() {
//        return fullName;
//    }
//
//    public String getDepartmentName() {
//        return departmentName;
//    }
//}
