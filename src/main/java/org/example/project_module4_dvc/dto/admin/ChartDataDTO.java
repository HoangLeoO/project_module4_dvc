package org.example.project_module4_dvc.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChartDataDTO {
    private List<String> labels;               // domains
    private List<ChartDatasetDTO> datasets;    // dữ liệu theo status
}
