package org.example.project_module4_dvc.service.cat;

import org.example.project_module4_dvc.dto.timeline.IWorkflowStepProjectionDTO;
import org.example.project_module4_dvc.repository.cat.CatWorkflowStepRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class CatWorkflowStepService implements ICatWorkflowStepService{
    @Autowired
    private CatWorkflowStepRepository workflowRepo;
    @Override
    public List<IWorkflowStepProjectionDTO> getWorkflowSteps(Long dossierId) {
        return workflowRepo.findWorkflowSteps(dossierId);
    }
}
