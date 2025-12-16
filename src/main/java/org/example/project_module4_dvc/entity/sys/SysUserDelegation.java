package org.example.project_module4_dvc.entity.sys;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sys_user_delegations")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SysUserDelegation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // --- Người ủy quyền (From) ---
    @NotNull(message = "Người ủy quyền không được để trống")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_user_id", nullable = false, referencedColumnName = "id")
    private SysUser fromUser;

    // --- Người nhận ủy quyền (To) ---
    @NotNull(message = "Người được ủy quyền không được để trống")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_user_id", nullable = false, referencedColumnName = "id")
    private SysUser toUser;

    // --- Thời gian ---

    @Column(name = "start_time", nullable = false)
    @NotNull(message = "Thời gian bắt đầu không được để trống")
    // Gợi ý: Nếu tạo mới, thời gian bắt đầu thường là hiện tại hoặc tương lai
    // @FutureOrPresent(message = "Thời gian bắt đầu phải từ hiện tại trở đi")
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    @NotNull(message = "Thời gian kết thúc không được để trống")
    // Gợi ý: Thời gian kết thúc phải ở tương lai
    // @Future(message = "Thời gian kết thúc phải ở tương lai")
    private LocalDateTime endTime;

    @Column(name = "notes")
    @Size(max = 255, message = "Ghi chú không được vượt quá 255 ký tự")
    private String notes;

    /*
     * Lưu ý Logic nghiệp vụ (Nên xử lý ở tầng Service hoặc Custom Validator):
     * 1. fromUser phải khác toUser (Không tự ủy quyền cho chính mình).
     * 2. endTime phải lớn hơn startTime.
     */
}