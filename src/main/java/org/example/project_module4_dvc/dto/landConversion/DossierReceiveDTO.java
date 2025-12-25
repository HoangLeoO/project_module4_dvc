package org.example.project_module4_dvc.dto.landConversion;

import lombok.Data;

@Data
public class DossierReceiveDTO {
    private Long dossierId;
    private Long handlerId;
    private String action; // ACCEPT, REJECT, REQUIRE_SUPPLEMENT
    private String comments;
    private Long nextHandlerId; // ID cán bộ tiếp theo
}
