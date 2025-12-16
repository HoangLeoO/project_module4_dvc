package org.example.project_module4_dvc.entity.cat;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "cat_knowledge_base")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CatKnowledgeBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // --- Quan hệ: Dịch vụ (Optional) ---
    // Có Constraint FK trong SQL -> Map sang Object CatService
    // Cho phép NULL (service_id có thể null) -> Không thêm @NotNull validation
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", referencedColumnName = "id")
    private CatService service;

    @Column(name = "title", nullable = false)
    @NotBlank(message = "Tiêu đề bài viết không được để trống")
    @Size(max = 255, message = "Tiêu đề không được vượt quá 255 ký tự")
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;
}