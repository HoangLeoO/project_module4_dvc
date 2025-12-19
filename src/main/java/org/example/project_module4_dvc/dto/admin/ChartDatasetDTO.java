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
public class ChartDatasetDTO {
    private String label;      // status
    private List<Long> data;   // số lượng
}
