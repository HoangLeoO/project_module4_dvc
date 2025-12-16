package org.example.project_module4_dvc.entity.sys;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "sys_configs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SysConfig {

    // Đây là khóa chính dạng String (Manual ID), không phải tự tăng
    @Id
    @Column(name = "config_key", nullable = false, length = 100)
    @NotBlank(message = "Mã cấu hình (Key) không được để trống")
    @Size(max = 100, message = "Mã cấu hình không được vượt quá 100 ký tự")
    private String configKey;

    @Column(name = "config_value", columnDefinition = "TEXT")
    // Không bắt buộc NotBlank vì có thể có cấu hình giá trị rỗng
    private String configValue;

    @Column(name = "description")
    @Size(max = 255, message = "Mô tả không được vượt quá 255 ký tự")
    private String description;
}