package org.example.project_module4_dvc.entity.ops;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "ops_dossier_files")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpsDossierFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // --- Quan hệ: Thuộc về Hồ sơ nào ---
    @NotNull(message = "Hồ sơ không được để trống")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dossier_id", nullable = false, referencedColumnName = "id")
    // Annotation này giúp Hibernate hiểu là DB đã có ràng buộc ON DELETE CASCADE
    // Giúp tối ưu hiệu năng khi xóa (Hibernate sẽ không cần issue lệnh delete cho từng file con)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private OpsDossier dossier;

    @Column(name = "file_name", nullable = false)
    @NotBlank(message = "Tên tệp không được để trống")
    @Size(max = 255, message = "Tên tệp không được vượt quá 255 ký tự")
    private String fileName;

    @Column(name = "file_url", nullable = false, length = 500)
    @NotBlank(message = "Đường dẫn tệp không được để trống")
    @Size(max = 500, message = "Đường dẫn tệp không được vượt quá 500 ký tự")
    private String fileUrl;

    @Column(name = "file_type", nullable = false, length = 20)
    @NotBlank(message = "Loại tệp không được để trống")
    @Size(max = 20, message = "Loại tệp không được vượt quá 20 ký tự")
    private String fileType; // PDF, DOCX, JPG...
}