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

    // Nhúng Composite Key vào đây
    @EmbeddedId
    private SysUserRoleId id;

    // --- Mối quan hệ với User ---
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId") // Map field này vào thuộc tính 'userId' của class SysUserRoleId
    @JoinColumn(name = "user_id")
    private SysUser user;

    // --- Mối quan hệ với Role ---
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("roleId") // Map field này vào thuộc tính 'roleId' của class SysUserRoleId
    @JoinColumn(name = "role_id")
    private SysRole role;

    // Lưu ý: SQL hiện tại của bạn không có cột created_at,
    // nếu sau này thêm cột 'granted_date' thì khai báo thêm ở dưới đây dễ dàng.
}