package org.example.project_module4_dvc.entity.mod;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.example.project_module4_dvc.entity.sys.SysUser;

@Entity
@Table(name = "mod_personal_vaults")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModPersonalVault {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // --- Quan hệ: Người sở hữu (FK) ---
    @NotNull(message = "Người dùng không được để trống")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id")
    private SysUser user;

    @Column(name = "doc_name", nullable = false, length = 100)
    @NotBlank(message = "Tên tài liệu không được để trống")
    @Size(max = 100, message = "Tên tài liệu không được vượt quá 100 ký tự")
    private String docName;

    @Column(name = "doc_type", nullable = false, length = 50)
    @NotBlank(message = "Loại tài liệu không được để trống")
    @Size(max = 50, message = "Loại tài liệu không được vượt quá 50 ký tự")
    private String docType;

    @Column(name = "file_url", nullable = false, length = 500)
    @NotBlank(message = "Đường dẫn tệp không được để trống")
    @Size(max = 500, message = "Đường dẫn tệp không được vượt quá 500 ký tự")
    private String fileUrl;
}