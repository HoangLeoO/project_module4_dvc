package org.example.project_module4_dvc.entity.mock;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.example.project_module4_dvc.entity.base.BaseEntity;

@Entity
@Table(name = "mock_households")
@Getter
@Setter
@SuperBuilder // Kế thừa SuperBuilder từ BaseEntity
@NoArgsConstructor
public class MockHousehold extends BaseEntity {

    @Column(name = "household_code", nullable = false, unique = true, length = 20)
    @NotBlank(message = "Số sổ hộ khẩu không được để trống")
    @Size(max = 20, message = "Số sổ hộ khẩu không được vượt quá 20 ký tự")
    private String householdCode;

    @Column(name = "address", nullable = false)
    @NotBlank(message = "Địa chỉ hộ khẩu không được để trống")
    private String address;

    // Mapping Foreign Key: head_citizen_id -> MockCitizen
    // Sử dụng FetchType.LAZY để khi query Hộ khẩu thì chưa load ngay thông tin Chủ hộ (tối ưu hiệu năng)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "head_citizen_id", referencedColumnName = "id")
    private MockCitizen headCitizen;
    // Lưu ý: BaseEntity đã có sẵn 'id' và 'createdAt'
}