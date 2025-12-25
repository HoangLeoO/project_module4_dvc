package org.example.project_module4_dvc.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminUserListDTO {
    private Long id;
    private String username;
    private String fullName;
    private String departmentName;
    private List<String> roles;
}
