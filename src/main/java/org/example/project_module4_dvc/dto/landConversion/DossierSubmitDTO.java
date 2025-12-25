package org.example.project_module4_dvc.dto.landConversion;

import lombok.Data;
import java.util.List;

@Data
public class DossierSubmitDTO {
    private Long serviceId;
    private Long applicantId;
    private Long receivingDeptId;
    private String formData; // JSON string
    private List<FileUploadDTO> files;
}


