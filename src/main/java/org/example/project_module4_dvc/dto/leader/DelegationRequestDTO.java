package org.example.project_module4_dvc.dto.leader;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DelegationRequestDTO {
    private Long delegateeId;
    private LocalDate fromDate;
    private LocalDate toDate;
    
    // "FULL" or "CUSTOM"
    private boolean isFullWith;
    
    // List of selected scope values (e.g. "DOMAIN:Đất đai", "SERVICE:SV001")
    private List<String> selectedScopes;
}
