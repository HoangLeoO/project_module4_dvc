package org.example.project_module4_dvc.dto.landConversion;

import lombok.Data;

@Data
public class DossierAppraisalDTO {
    private Long dossierId;
    private Long officerId;
    private String result; // PASS, FAIL
    private String comments;
}