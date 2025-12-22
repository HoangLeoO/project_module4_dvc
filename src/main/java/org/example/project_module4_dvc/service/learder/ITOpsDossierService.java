package org.example.project_module4_dvc.service.learder;

import org.example.project_module4_dvc.dto.dossier.NewDossierDTO;
import org.example.project_module4_dvc.entity.sys.SysUser;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ITOpsDossierService {
    void submitDossier(NewDossierDTO request, List<MultipartFile> files, SysUser currentUser) throws Exception;
}
