package org.example.project_module4_dvc.entity.mock;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "mock_lands")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MockLand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // --- 1. Thông tin pháp lý ---
    @Column(name = "land_certificate_number", nullable = false, unique = true, length = 50)
    @NotBlank(message = "Số sổ đỏ không được để trống")
    @Size(max = 50, message = "Số sổ đỏ quá dài")
    private String landCertificateNumber;

    @Column(name = "issue_date")
    @PastOrPresent(message = "Ngày cấp không hợp lệ")
    private LocalDate issueDate;

    @Column(name = "issue_authority", length = 100)
    private String issueAuthority;

    // --- 2. Thông tin vị trí ---
    @Column(name = "map_sheet_number", length = 20)
    private String mapSheetNumber;

    @Column(name = "parcel_number", length = 20)
    private String parcelNumber;

    @Column(name = "address_detail")
    private String addressDetail;

    // --- 3. Thông tin về đất ---
    // DECIMAL(10, 2) -> Mapping sang BigDecimal trong Java
    @Column(name = "area_m2", precision = 10, scale = 2)
    @PositiveOrZero(message = "Diện tích đất phải là số dương")
    private BigDecimal areaM2;

    @Column(name = "usage_form", length = 50)
    private String usageForm;

    @Column(name = "land_purpose", length = 100)
    private String landPurpose;

    @Column(name = "usage_period", length = 50)
    private String usagePeriod;

    // --- 4. Thông tin tài sản gắn liền với đất ---
    @Column(name = "house_area_m2", precision = 10, scale = 2)
    @PositiveOrZero(message = "Diện tích nhà ở phải là số dương")
    private BigDecimal houseAreaM2;

    @Column(name = "construction_area_m2", precision = 10, scale = 2)
    @PositiveOrZero(message = "Diện tích sàn xây dựng phải là số dương")
    private BigDecimal constructionAreaM2;

    @Column(name = "asset_notes", columnDefinition = "TEXT")
    private String assetNotes;

    // --- 5. Chủ sở hữu (Foreign Key) ---
    // Mapping Object Relation: Nhiều mảnh đất có thể thuộc về 1 chủ sở hữu
    @NotNull(message = "Chủ sở hữu không được để trống")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false, referencedColumnName = "id")
    private MockCitizen owner;

    // --- 6. Trạng thái ---
    @Column(name = "land_status", length = 50)
    private String landStatus;
}