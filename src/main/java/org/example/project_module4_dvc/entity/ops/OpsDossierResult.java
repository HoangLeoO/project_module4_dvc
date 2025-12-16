package org.example.project_module4_dvc.entity.ops;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.example.project_module4_dvc.entity.base.BaseEntity;

@Entity
@Table(name = "ops_dossier_results")
@Getter
@Setter
@SuperBuilder // Kế thừa SuperBuilder từ BaseEntity
@NoArgsConstructor
@AllArgsConstructor
public class OpsDossierResult extends BaseEntity {

    // --- Quan hệ: Hồ sơ gốc (Có Constraint FK) ---
    @NotNull(message = "Hồ sơ không được để trống")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dossier_id", nullable = false, referencedColumnName = "id")
    private OpsDossier dossier;

    @Column(name = "decision_number", nullable = false, unique = true, length = 50)
    @NotBlank(message = "Số quyết định không được để trống")
    @Size(max = 50, message = "Số quyết định không được vượt quá 50 ký tự")
    private String decisionNumber;

    @Column(name = "signer_name", length = 100)
    @Size(max = 100, message = "Tên người ký không được vượt quá 100 ký tự")
    private String signerName;

    @Column(name = "e_file_url", nullable = false, length = 500)
    @NotBlank(message = "Đường dẫn tệp kết quả không được để trống")
    @Size(max = 500, message = "Đường dẫn tệp không được vượt quá 500 ký tự")
    private String eFileUrl;

    // Lưu ý: BaseEntity đã có sẵn 'id' và 'createdAt'
}