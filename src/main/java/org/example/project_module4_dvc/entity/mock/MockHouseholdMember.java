package org.example.project_module4_dvc.entity.mock;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "mock_household_members",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_citizen_active", columnNames = {"citizen_id", "status"})
        })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MockHouseholdMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;


    @NotNull(message = "Sổ hộ khẩu không được để trống")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "household_id", nullable = false, referencedColumnName = "id")
    private MockHousehold household;


    @NotNull(message = "Công dân không được để trống")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "citizen_id", nullable = false, referencedColumnName = "id")
    private MockCitizen citizen;

    @Column(name = "relation_to_head", nullable = false, length = 50)
    @NotBlank(message = "Quan hệ với chủ hộ không được để trống")
    @Size(max = 50, message = "Quan hệ không được vượt quá 50 ký tự")
    private String relationToHead;

    @Column(name = "move_in_date")
    private LocalDate moveInDate;

    @Column(name = "status")
    @Builder.Default
    private Integer status = 1;
}

