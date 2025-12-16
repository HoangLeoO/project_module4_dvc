package org.example.project_module4_dvc.entity.mod;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.example.project_module4_dvc.entity.ops.OpsDossier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "mod_payments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // --- Quan hệ: Hồ sơ (FK) ---
    // SQL có constraint fk_pay_dossier -> Map Object
    @NotNull(message = "Hồ sơ không được để trống")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dossier_id", nullable = false, referencedColumnName = "id")
    private OpsDossier dossier;

    // DECIMAL(15, 2) -> BigDecimal
    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    @NotNull(message = "Số tiền không được để trống")
    @PositiveOrZero(message = "Số tiền phải lớn hơn hoặc bằng 0")
    private BigDecimal amount;

    @Column(name = "receipt_number", length = 50)
    @Size(max = 50, message = "Số biên lai không được vượt quá 50 ký tự")
    private String receiptNumber;

    @Column(name = "payment_status", length = 20)
    @Builder.Default
    private String paymentStatus = "PENDING";

    @Column(name = "pay_date")
    private LocalDateTime payDate;
}