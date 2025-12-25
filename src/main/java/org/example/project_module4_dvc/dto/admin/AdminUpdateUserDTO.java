package org.example.project_module4_dvc.dto.admin;

import lombok.Data;
import java.util.List;

@Data
public class AdminUpdateUserDTO {
    private Long userId;
    private Long deptId;
    private List<Long> roleIds;
}
