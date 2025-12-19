package org.example.project_module4_dvc.service.mod;

import lombok.RequiredArgsConstructor;
import org.example.project_module4_dvc.entity.mod.ModFeedback;
import org.example.project_module4_dvc.repository.mod.ModFeedbackRepository;
import org.example.project_module4_dvc.service.iml.IModFeedbackService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ModFeedbackService implements IModFeedbackService {

    private  final ModFeedbackRepository modFeedbackRepository;
    // dếm tổng số phản hồi
    @Override
    public long totalFeedbacks() {
        return modFeedbackRepository.count();
    }
    // đếm số phản hồi đã được giải quyết
    @Override
    public long resolvedFeedbacks() {
        return modFeedbackRepository.countByIsResolvedTrue();
    }
// đếm số phản hồi chưa được giải quyết
    @Override
    public long unresolvedFeedbacks() {
        return modFeedbackRepository.countByIsResolvedFalse();
    }
// lấy tất cả phản hồi, sắp xếp theo ngày tạo giảm dần
    @Override
    public List<ModFeedback> getAllFeedbacks() {
        return modFeedbackRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }
    // lấy danh sách phản hồi chưa được giải quyết, sắp xếp theo ngày tạo giảm dần
@Override
    public List<ModFeedback> getUnresolvedFeedbacks() {
        return modFeedbackRepository.findByIsResolvedFalseOrderByCreatedAtDesc();
    }

    @Override
    public Page<ModFeedback> getUnresolvedFeedbacks(Pageable pageable) {
        return modFeedbackRepository.findByIsResolvedFalse(pageable);
    }
}
