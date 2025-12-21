package org.example.project_module4_dvc.dto.leader;

import lombok.*;
import org.example.project_module4_dvc.entity.sys.SysUserDelegation;
import org.example.project_module4_dvc.entity.sys.SysUser;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DelegationConfigDTO {
    private List<SysUser> potentialDelegatees;
    private List<SysUserDelegation> currentDelegations;
}
