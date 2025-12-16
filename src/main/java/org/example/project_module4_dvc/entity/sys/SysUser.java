package org.example.project_module4_dvc.entity.sys;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.example.project_module4_dvc.entity.base.BaseEntity;
import org.example.project_module4_dvc.entity.mock.MockCitizen;

@Entity
@Table(name = "sys_users")
@Getter
@Setter
@SuperBuilder // Kế thừa SuperBuilder từ BaseEntity
@NoArgsConstructor
public class SysUser extends BaseEntity {

    @Column(name = "username", nullable = false, unique = true, length = 50)
    @NotBlank(message = "Tên đăng nhập không được để trống")
    @Size(min = 3, max = 50, message = "Tên đăng nhập phải từ 3 đến 50 ký tự")
    private String username;

    @Column(name = "password_hash", nullable = false)
    @NotBlank(message = "Mật khẩu không được để trống")
    // Không giới hạn max size cụ thể vì hash (như BCrypt) thường có độ dài cố định,
    // nhưng DB set 255 là an toàn.
    private String passwordHash;

    @Column(name = "full_name", nullable = false, length = 100)
    @NotBlank(message = "Họ và tên không được để trống")
    @Size(max = 100, message = "Họ và tên không được vượt quá 100 ký tự")
    private String fullName;

    // Không dùng Enum theo yêu cầu, map trực tiếp String
    @Column(name = "user_type", nullable = false, length = 20)
    @NotBlank(message = "Loại người dùng không được để trống")
    // Gợi ý: Có thể dùng @Pattern để validate nếu cần thiết
    // @Pattern(regexp = "CITIZEN|OFFICIAL|ADMIN", message = "Loại người dùng không hợp lệ")
    private String userType;

    // --- Relationship: Liên kết hồ sơ công dân (Optional) ---
    // Dùng FetchType.LAZY để khi load User đăng nhập không bị query thừa sang bảng Citizen nếu không cần
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "citizen_id", referencedColumnName = "id")
    private MockCitizen citizen;

    // --- Relationship: Liên kết phòng ban (Optional - cho cán bộ) ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dept_id", referencedColumnName = "id")
    private SysDepartment department;
}