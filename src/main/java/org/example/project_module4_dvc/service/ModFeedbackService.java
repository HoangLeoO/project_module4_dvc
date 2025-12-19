package org.example.project_module4_dvc.service;

import lombok.RequiredArgsConstructor;
import org.example.project_module4_dvc.entity.mod.ModFeedback;
import org.example.project_module4_dvc.repository.mod.ModFeedbackRepository;
import org.example.project_module4_dvc.service.iml.IModFeedbackService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ModFeedbackService implements IModFeedbackService {

    private  final ModFeedbackRepository modFeedbackRepository;

    @Override
    public long totalFeedbacks() {
        return modFeedbackRepository.count();
    }

    @Override
    public long resolvedFeedbacks() {
        return modFeedbackRepository.countByIsResolvedTrue();
    }

    @Override
    public long unresolvedFeedbacks() {
        return modFeedbackRepository.countByIsResolvedFalse();
    }

    @Override
    public List<ModFeedback> getAllFeedbacks() {
        return modFeedbackRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }
@Override
    public List<ModFeedback> getUnresolvedFeedbacks() {
        return modFeedbackRepository.findByIsResolvedFalseOrderByCreatedAtDesc();
    }
}
