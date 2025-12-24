package org.example.project_module4_dvc.entity.mock;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.example.project_module4_dvc.entity.base.AuditableEntity;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "mock_citizen_relationships", schema = "egov_db")
public class MockCitizenRelationship extends AuditableEntity {
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "citizen_id", nullable = false)
    private MockCitizen citizen;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "relative_id", nullable = false)
    private MockCitizen relative;

    @Size(max = 50)
    @NotNull
    @Column(name = "relationship_type", nullable = false, length = 50)
    private String relationshipType;

}