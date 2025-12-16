package org.example.project_module4_dvc.entity.sys;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.example.project_module4_dvc.entity.base.BaseEntity;

@Entity
@Table(name = "sys_audit_logs")
@Getter
@Setter
@SuperBuilder // Kế thừa SuperBuilder từ BaseEntity
@NoArgsConstructor
@AllArgsConstructor
public class SysAuditLog extends BaseEntity {

    // --- Người thực hiện ---
    // Mapping sang SysUser.
    // Lưu ý: Có thể null (ví dụ: request từ hệ thống cronjob hoặc user chưa đăng nhập)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private SysUser user;

    @Column(name = "endpoint")
    @Size(max = 255, message = "Endpoint không được vượt quá 255 ký tự")
    private String endpoint;

    @Column(name = "method", length = 10)
    @Size(max = 10, message = "Method không được vượt quá 10 ký tự")
    private String method; // GET, POST, PUT, DELETE

    @Column(name = "status_code")
    private Integer statusCode; // 200, 400, 500

    @Column(name = "payload", columnDefinition = "TEXT")
    private String payload;
}