package org.example.project_module4_dvc.entity.mock;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.example.project_module4_dvc.entity.base.AuditableEntity;

@Entity
@Table(name = "mock_citizen_relationships", uniqueConstraints = @UniqueConstraint(name = "uq_citizen_relative", columnNames = {
        "citizen_id", "relative_id" }))
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MockCitizenRelationship extends AuditableEntity {

    @Column(name = "citizen_id", nullable = false)
    @NotNull(message = "ID công dân không được để trống")
    private Long citizenId;

    @Column(name = "relative_id", nullable = false)
    @NotNull(message = "ID người thân không được để trống")
    private Long relativeId;

    @Column(name = "relationship_type", nullable = false, length = 50)
    @NotBlank(message = "Loại mối quan hệ không được để trống")
    private String relationshipType;

    // Foreign key relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "citizen_id", foreignKey = @ForeignKey(name = "fk_mcr_citizen"), insertable = false, updatable = false)
    private MockCitizen citizen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "relative_id", foreignKey = @ForeignKey(name = "fk_mcr_relative"), insertable = false, updatable = false)
    private MockCitizen relative;
}
