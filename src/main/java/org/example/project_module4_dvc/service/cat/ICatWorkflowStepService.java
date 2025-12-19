package org.example.project_module4_dvc.service.cat;

import org.example.project_module4_dvc.dto.timeline.IWorkflowStepProjectionDTO;

import java.util.List;

public interface ICatWorkflowStepService {
    List<IWorkflowStepProjectionDTO> getWorkflowSteps(Long dossierId);
}
