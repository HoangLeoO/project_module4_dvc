package org.example.project_module4_dvc.entity.mock;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "mock_businesses")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MockBusiness {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "tax_code", nullable = false, unique = true, length = 20)
    @NotBlank(message = "Mã số thuế không được để trống")
    @Size(max = 20, message = "Mã số thuế không được vượt quá 20 ký tự")
    // Bạn có thể mở comment dòng dưới nếu muốn bắt buộc MST chỉ chứa số
    // @Pattern(regexp = "\\d+", message = "Mã số thuế chỉ được chứa ký tự số")
    private String taxCode;

    @Column(name = "business_name", nullable = false, length = 200)
    @NotBlank(message = "Tên doanh nghiệp không được để trống")
    @Size(max = 200, message = "Tên doanh nghiệp không được vượt quá 200 ký tự")
    private String businessName;

    // DECIMAL(15, 2) -> BigDecimal
    @Column(name = "capital", precision = 15, scale = 2)
    @PositiveOrZero(message = "Vốn điều lệ phải là số dương hoặc bằng 0")
    private BigDecimal capital;

    // --- Relationship: Người đại diện pháp luật ---
    @NotNull(message = "Người đại diện/Chủ sở hữu không được để trống")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false, referencedColumnName = "id")
    private MockCitizen owner;

    @Column(name = "address")
    private String address;

    @Column(name = "business_lines")
    private String businessLines;
}