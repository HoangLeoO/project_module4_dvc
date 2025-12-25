package org.example.project_module4_dvc.dto.admin;

import lombok.Data;

import java.util.List;

@Data
public class AdminCreateUserDTO {
    private Long citizenId;
    private String username;
    private String password;
    private String fullName; // Optional, can fetch from Citizen
    private Long deptId;
    private List<Long> roleIds;
}
