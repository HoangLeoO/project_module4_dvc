package org.example.project_module4_dvc.entity.cat;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "cat_workflow_steps")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CatWorkflowStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // --- Có FK trong SQL -> Dùng @ManyToOne ---
    @NotNull(message = "Dịch vụ không được để trống")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false, referencedColumnName = "id")
    private CatService service;

    @Column(name = "step_name", nullable = false, length = 100)
    @NotBlank(message = "Tên bước xử lý không được để trống")
    @Size(max = 100, message = "Tên bước không được vượt quá 100 ký tự")
    private String stepName;

    @Column(name = "step_order", nullable = false)
    @NotNull(message = "Thứ tự bước không được để trống")
    @Min(value = 1, message = "Thứ tự bước phải lớn hơn hoặc bằng 1")
    private Integer stepOrder;

    // --- KHÔNG có FK trong SQL -> Dùng Long thuần túy ---
    // Đây chỉ là một trường lưu ID, không tạo quan hệ JPA
    @Column(name = "role_required_id")
    private Long roleRequiredId;
}