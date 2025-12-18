package org.example.project_module4_dvc.entity.cat;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.example.project_module4_dvc.converter.JsonToMapConverter;

import java.util.Map;

@Entity
@Table(name = "cat_templates")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CatTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // --- Quan hệ: Dịch vụ sở hữu mẫu in này ---
    @NotNull(message = "Dịch vụ không được để trống")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false, referencedColumnName = "id")
    private CatService service;

    @Column(name = "template_name", nullable = false, length = 100)
    @NotBlank(message = "Tên biểu mẫu không được để trống")
    @Size(max = 100, message = "Tên biểu mẫu không được vượt quá 100 ký tự")
    private String templateName;

    @Column(name = "file_path", nullable = false)
    @NotBlank(message = "Đường dẫn file mẫu không được để trống")
    @Size(max = 255, message = "Đường dẫn file quá dài")
    private String filePath;

    @Column(name = "variable_mapping", columnDefinition = "json")
    @Convert(converter = JsonToMapConverter.class)
    private Map<String, Object> variableMapping;
}