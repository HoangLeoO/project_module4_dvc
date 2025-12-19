package org.example.project_module4_dvc.service.mod;

import org.example.project_module4_dvc.entity.mod.ModFeedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IModFeedbackService {
    long totalFeedbacks();

    long resolvedFeedbacks();

    long unresolvedFeedbacks();

    List<ModFeedback> getAllFeedbacks();

    List<ModFeedback> getUnresolvedFeedbacks();

    Page<ModFeedback> getUnresolvedFeedbacks(Pageable pageable);
}
