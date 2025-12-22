package org.example.project_module4_dvc.dto.specialist;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SpecialistAvailableDTO {
    private Long userId;
    private String fullName;
    private String username;
    private String deptName;
    private String roleName;
    private Long currentWorkload;
}
