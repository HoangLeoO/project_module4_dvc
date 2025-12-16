package org.example.project_module4_dvc.entity.sys;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sys_departments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SysDepartment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "dept_code", nullable = false, unique = true, length = 50)
    @NotBlank(message = "Mã phòng ban không được để trống")
    @Size(max = 50, message = "Mã phòng ban không được vượt quá 50 ký tự")
    private String deptCode;

    @Column(name = "dept_name", nullable = false, length = 100)
    @NotBlank(message = "Tên phòng ban không được để trống")
    @Size(max = 100, message = "Tên phòng ban không được vượt quá 100 ký tự")
    private String deptName;

    // --- 1. Mối quan hệ với cấp trên (Parent) ---
    // Mapping: parent_id -> SysDepartment
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id") // Khóa ngoại trỏ về chính bảng này
    private SysDepartment parent;

    // --- 2. Mối quan hệ với cấp dưới (Children - Optional) ---
    // Field này không tạo cột trong DB, chỉ giúp Java lấy danh sách con dễ dàng
    // mappedBy = "parent": ánh xạ ngược lại field 'parent' ở trên
    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private List<SysDepartment> children = new ArrayList<>();

    @Column(name = "level")
    @Builder.Default
    @Min(value = 1, message = "Cấp độ phòng ban thấp nhất là 1")
    private Integer level = 1;

    // Helper method: Thêm phòng ban con (Tiện ích khi code)
    public void addChild(SysDepartment child) {
        children.add(child);
        child.setParent(this);
    }
}