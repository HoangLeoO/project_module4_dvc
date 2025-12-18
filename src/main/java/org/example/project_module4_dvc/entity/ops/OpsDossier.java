package org.example.project_module4_dvc.entity.ops;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.example.project_module4_dvc.entity.cat.CatService;
import org.example.project_module4_dvc.entity.sys.SysDepartment;
import org.example.project_module4_dvc.entity.sys.SysUser;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "ops_dossiers")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpsDossier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "dossier_code", nullable = false, unique = true, length = 50)
    @NotBlank(message = "Mã hồ sơ không được để trống")
    @Size(max = 50, message = "Mã hồ sơ không được vượt quá 50 ký tự")
    private String dossierCode;

    // --- Quan hệ: Dịch vụ công (FK) ---
    @NotNull(message = "Dịch vụ không được để trống")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false, referencedColumnName = "id")
    private CatService service;

    // --- Quan hệ: Người nộp hồ sơ (FK -> SysUser) ---
    @NotNull(message = "Người nộp hồ sơ không được để trống")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id", nullable = false, referencedColumnName = "id")
    private SysUser applicant;

    // --- Quan hệ: Cán bộ thụ lý (FK -> SysUser) ---
    // Có thể null (nếu hồ sơ chưa được phân công)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_handler_id", referencedColumnName = "id")
    private SysUser currentHandler;

    // ============================================================================
    // --- PHÒNG BAN TIẾP NHẬN HỒ SƠ (RECEIVING DEPARTMENT) ---
    // ============================================================================

    /**
     * Phòng ban/UBND xã phường tiếp nhận hồ sơ
     * Bắt buộc phải có để xác định địa bàn xử lý
     */
    @NotNull(message = "Đơn vị tiếp nhận hồ sơ không được để trống")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiving_dept_id", nullable = false, referencedColumnName = "id")
    private SysDepartment receivingDepartment;

    /**
     * Địa điểm tiếp nhận cụ thể (tùy chọn)
     * VD: "UBND xã Hòa Tiến, Huyện Hòa Vang, TP Đà Nẵng"
     */
    @Column(name = "receiving_location", length = 255)
    @Size(max = 255, message = "Địa điểm tiếp nhận không được vượt quá 255 ký tự")
    private String receivingLocation;


    // --- Trạng thái & Dữ liệu ---
    @Column(name = "dossier_status", length = 20)
    @Builder.Default
    private String dossierStatus = "NEW";

    @Column(name = "submission_date", updatable = false)
    @CreationTimestamp // Tự động lấy giờ hệ thống khi insert (giống DEFAULT CURRENT_TIMESTAMP)
    private LocalDateTime submissionDate;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "finish_date")
    private LocalDateTime finishDate;

    @Column(name = "form_data", columnDefinition = "json")
    private String formData;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;
}