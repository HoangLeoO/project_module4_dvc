package org.example.project_module4_dvc.entity.mod;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.example.project_module4_dvc.entity.base.BaseEntity;
import org.example.project_module4_dvc.entity.sys.SysUser;

@Entity
@Table(name = "mod_notifications")
@Getter
@Setter
@SuperBuilder // Kế thừa SuperBuilder từ BaseEntity
@NoArgsConstructor
@AllArgsConstructor
public class ModNotification extends BaseEntity {

    // --- Quan hệ: Người nhận (FK) ---
    @NotNull(message = "Người nhận không được để trống")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id")
    private SysUser user;

    @Column(name = "title", length = 200)
    @Size(max = 200, message = "Tiêu đề không được vượt quá 200 ký tự")
    private String title;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Column(name = "is_read")
    @Builder.Default
    private Boolean isRead = false;

    @Column(name = "type", length = 50)
    @Size(max = 50, message = "Loại thông báo không được vượt quá 50 ký tự")
    private String type;

    // BaseEntity đã có created_at
}