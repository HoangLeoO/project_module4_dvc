package org.example.project_module4_dvc.entity.mod;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "mod_feedback_attachments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModFeedbackAttachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // --- Quan hệ: Phản ánh (FK + Cascade Delete) ---
    @NotNull(message = "Phản ánh không được để trống")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feedback_id", nullable = false, referencedColumnName = "id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ModFeedback feedback;

    @Column(name = "file_url", nullable = false, length = 500)
    @NotBlank(message = "Đường dẫn tệp không được để trống")
    @Size(max = 500, message = "Đường dẫn tệp không được vượt quá 500 ký tự")
    private String fileUrl;
}