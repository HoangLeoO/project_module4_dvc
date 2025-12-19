package org.example.project_module4_dvc.repository.mod;
import org.example.project_module4_dvc.entity.mod.ModFeedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModFeedbackRepository extends JpaRepository<ModFeedback, Long> {
    // đếm số phản hồi đã được giải quyết
    long countByIsResolvedTrue();
    // đếm số phản hồi chưa được giải quyết
    long countByIsResolvedFalse();
    // lấy danh sách phản hồi chưa được giải quyết, sắp xếp theo ngày tạo giảm dần
    List<ModFeedback> findByIsResolvedFalseOrderByCreatedAtDesc();
    // lấy trang phản hồi chưa được giải quyết
    Page<ModFeedback> findByIsResolvedFalse(Pageable pageable);
}