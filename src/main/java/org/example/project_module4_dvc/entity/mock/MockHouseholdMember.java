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
                // Đảm bảo: Một công dân (citizen_id) chỉ có thể có 1 trạng thái (status) tại 1 thời điểm.
                // Ví dụ: Không thể vừa 'Active' ở hộ khẩu A vừa 'Active' ở hộ khẩu B.
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

    // --- Relationship: N Members thuộc về 1 Household ---
    @NotNull(message = "Sổ hộ khẩu không được để trống")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "household_id", nullable = false, referencedColumnName = "id")
    private MockHousehold household;

    // --- Relationship: N Members liên kết với 1 Citizen ---
    // (Thực tế là 1-1 tại một thời điểm active, nhưng lịch sử có thể nhiều lần chuyển hộ khẩu nên dùng N-1)
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
    private Integer status = 1; // 1: Đang
}

