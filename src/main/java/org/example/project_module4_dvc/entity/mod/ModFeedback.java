package org.example.project_module4_dvc.entity.mod;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.example.project_module4_dvc.entity.base.BaseEntity;
import org.example.project_module4_dvc.entity.sys.SysUser;

@Entity
@Table(name = "mod_feedbacks")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ModFeedback extends BaseEntity {


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private SysUser user;


    @Column(name = "dossier_id")
    private Long dossierId;

    @Column(name = "title", nullable = false, length = 200)
    @NotBlank(message = "Tiêu đề không được để trống")
    @Size(max = 200, message = "Tiêu đề không được vượt quá 200 ký tự")
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "rating")
    @Min(value = 1, message = "Đánh giá thấp nhất là 1 sao")
    @Max(value = 5, message = "Đánh giá cao nhất là 5 sao")
    private Integer rating;

    @Column(name = "is_resolved")
    @Builder.Default
    private Boolean isResolved = false;

}