package org.example.project_module4_dvc.dto.view;

import lombok.Getter;
import lombok.Setter;
import org.example.project_module4_dvc.entity.ops.OpsDossier;
import org.example.project_module4_dvc.entity.sys.SysUser;
import org.example.project_module4_dvc.entity.cat.CatService;

import java.util.Map;

@Getter
@Setter
public class DossierDetailViewDTO {
    private Long id;
    private String dossierCode;
    private String applicantFullName;
    private String cccd;
    private String serviceName;
    private String dossierStatus;
    private Map<String, Object> formData;
    private SysUser applicant; // For accessing other applicant fields if needed
    private CatService service; // For accessing other service fields if needed
    private java.util.List<org.example.project_module4_dvc.entity.ops.OpsDossierFile> files;

    public DossierDetailViewDTO(OpsDossier dossier) {
        this.id = dossier.getId();
        this.dossierCode = dossier.getDossierCode();
        this.dossierStatus = dossier.getDossierStatus();
        this.formData = dossier.getFormData();

        if (dossier.getApplicant() != null) {
            this.applicant = dossier.getApplicant();
            this.applicantFullName = dossier.getApplicant().getFullName();
            if (dossier.getApplicant().getCitizen() != null) {
                this.cccd = dossier.getApplicant().getCitizen().getCccd();
            } else {
                this.cccd = "";
            }
        }

        if (dossier.getService() != null) {
            this.service = dossier.getService();
            this.serviceName = dossier.getService().getServiceName();
        }
    }
}
