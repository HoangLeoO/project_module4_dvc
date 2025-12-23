package org.example.project_module4_dvc.dto.landConversion;

import lombok.Data;

@Data
public class DossierApprovalDTO {
    private Long dossierId;
    private Long chairmanId;
    private String decision; // APPROVE, REJECT
    private String comments;
}