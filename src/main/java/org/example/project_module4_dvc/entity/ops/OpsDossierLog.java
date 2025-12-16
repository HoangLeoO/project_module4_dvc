package org.example.project_module4_dvc.entity.ops;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.example.project_module4_dvc.entity.base.BaseEntity;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "ops_dossier_logs")
@Getter
@Setter
@SuperBuilder // Kế thừa SuperBuilder từ BaseEntity
@NoArgsConstructor
@AllArgsConstructor
public class OpsDossierLog extends BaseEntity {

    // --- Quan hệ: Hồ sơ (Có Constraint FK + Cascade Delete) ---
    @NotNull(message = "Hồ sơ không được để trống")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dossier_id", nullable = false, referencedColumnName = "id")
    @OnDelete(action = OnDeleteAction.CASCADE) // Báo cho Hibernate biết DB đã có rule này
    private OpsDossier dossier;

    // --- Người thực hiện (Không có Constraint FK trong SQL) ---
    // Map thành Long (Raw ID) để đúng với cấu trúc bảng
    @Column(name = "actor_id", nullable = false)
    @NotNull(message = "ID người thực hiện không được để trống")
    private Long actorId;

    @Column(name = "action", nullable = false, length = 50)
    @NotBlank(message = "Hành động không được để trống")
    @Size(max = 50, message = "Hành động không được vượt quá 50 ký tự")
    private String action; // VD: CHUYEN_BUOC, PHE_DUYET

    @Column(name = "prev_status", length = 20)
    @Size(max = 20)
    private String prevStatus;

    @Column(name = "next_status", length = 20)
    @Size(max = 20)
    private String nextStatus;

    @Column(name = "comments", columnDefinition = "TEXT")
    private String comments;

    // Lưu ý: BaseEntity đã có sẵn 'id' và 'createdAt' (tự động mapping)
}