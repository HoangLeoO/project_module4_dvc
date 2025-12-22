package org.example.project_module4_dvc.service.ops;

import org.example.project_module4_dvc.entity.ops.OpsDossierFile;
import org.example.project_module4_dvc.repository.ops.OpsDossierFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OpsDossierFileService implements IOpsDossierFileService {
    @Autowired
    private OpsDossierFileRepository opsDossierFileRepository;

    @Override
    public OpsDossierFile getById(Long id) {
        return opsDossierFileRepository.findById(id).orElse(null);
    }
}
