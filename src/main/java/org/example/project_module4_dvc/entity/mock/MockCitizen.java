package org.example.project_module4_dvc.entity.mock;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.example.project_module4_dvc.entity.base.AuditableEntity;

import java.time.LocalDate;

@Entity
@Table(name = "mock_citizens",
        indexes = @Index(name = "idx_mock_citizen_name", columnList = "full_name"))
@Getter
@Setter
@SuperBuilder // Quan trọng: Phải dùng SuperBuilder thay vì Builder thường
@NoArgsConstructor
@AllArgsConstructor
public class MockCitizen extends AuditableEntity {

    @Column(name = "cccd", nullable = false, unique = true, length = 12)
    @NotBlank(message = "Số CCCD không được để trống")
    @Size(min = 12, max = 12, message = "Số CCCD phải bao gồm đúng 12 ký tự")
    @Pattern(regexp = "\\d+", message = "Số CCCD chỉ được chứa ký tự số")
    private String cccd;

    @Column(name = "full_name", nullable = false, length = 100)
    @NotBlank(message = "Họ và tên không được để trống")
    @Size(max = 100, message = "Họ và tên không được vượt quá 100 ký tự")
    private String fullName;

    @Column(name = "dob", nullable = false)
    @NotNull(message = "Ngày sinh không được để trống")
    @Past(message = "Ngày sinh phải là ngày trong quá khứ")
    private LocalDate dob;

    // Đã thay Enum bằng String theo yêu cầu
    @Column(name = "gender", nullable = false, length = 10)
    @NotBlank(message = "Giới tính không được để trống")
    // Bạn có thể dùng @Pattern để giới hạn giá trị nhập vào nếu muốn
    // @Pattern(regexp = "MALE|FEMALE|OTHER", message = "Giới tính không hợp lệ")
    private String gender;

    @Column(name = "hometown")
    private String hometown;

    @Column(name = "ethnic_group", length = 50)
    private String ethnicGroup;

    @Column(name = "religion", length = 50)
    private String religion;

    @Column(name = "permanent_address")
    private String permanentAddress;

    @Column(name = "temporary_address")
    private String temporaryAddress;

    @Column(name = "fingerprint_data", columnDefinition = "TEXT")
    private String fingerprintData;

    @Column(name = "avatar_url")
    private String avatarUrl;

    // Đã thay Enum bằng String theo yêu cầu
    @Column(name = "marital_status", length = 20)
    @Builder.Default // Giá trị mặc định khi dùng Builder
    private String maritalStatus = "SINGLE";

    @Column(name = "spouse_id")
    private Long spouseId;

    @Column(name = "is_deceased")
    @Builder.Default
    private Boolean isDeceased = false;

    @Column(name = "status")
    @Builder.Default
    private Integer status = 1; // 1: Active, 0: Inactive
}