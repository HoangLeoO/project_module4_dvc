package org.example.project_module4_dvc.service.iml;

import org.example.project_module4_dvc.entity.mod.ModFeedback;

import java.util.List;

public interface IModFeedbackService {
    long totalFeedbacks();

    long resolvedFeedbacks();

    long unresolvedFeedbacks();

    List<ModFeedback> getAllFeedbacks();

    List<ModFeedback> getUnresolvedFeedbacks();
}
