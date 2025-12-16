package org.example.project_module4_dvc.entity.base;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
@SuperBuilder // Dùng SuperBuilder để hỗ trợ kế thừa
@NoArgsConstructor
@AllArgsConstructor
public abstract class AuditableEntity extends BaseEntity {
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}