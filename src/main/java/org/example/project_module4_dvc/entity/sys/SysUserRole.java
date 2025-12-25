package org.example.project_module4_dvc.entity.sys;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sys_user_roles")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SysUserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- Mối quan hệ với User ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private SysUser user;

    // --- Mối quan hệ với Role ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private SysRole role;
}