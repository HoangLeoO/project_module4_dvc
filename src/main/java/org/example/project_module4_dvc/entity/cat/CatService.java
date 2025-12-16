package org.example.project_module4_dvc.entity.cat;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "cat_services")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CatService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "service_code", nullable = false, unique = true, length = 50)
    @NotBlank(message = "Mã dịch vụ không được để trống")
    @Size(max = 50, message = "Mã dịch vụ không được vượt quá 50 ký tự")
    private String serviceCode;

    @Column(name = "service_name", nullable = false)
    @NotBlank(message = "Tên dịch vụ không được để trống")
    @Size(max = 255, message = "Tên dịch vụ quá dài")
    private String serviceName;

    @Column(name = "domain", nullable = false, length = 50)
    @NotBlank(message = "Lĩnh vực không được để trống")
    private String domain; // VD: "DAT_DAI", "HO_TICH"

    @Column(name = "sla_hours")
    @Builder.Default
    @Min(value = 0, message = "Thời gian xử lý không được âm")
    private Integer slaHours = 24;

    @Column(name = "fee_amount", precision = 15, scale = 2)
    @Builder.Default
    @PositiveOrZero(message = "Lệ phí phải là số dương hoặc bằng 0")
    private BigDecimal feeAmount = BigDecimal.ZERO;

    // Mapping cột JSON trong MySQL sang String trong Java
    // Khi trả về API, Spring sẽ tự escape chuỗi này.
    // Nếu muốn trả về JSON object thuần, cần xử lý ở DTO.
    @Column(name = "form_schema", columnDefinition = "json")
    private String formSchema;
}