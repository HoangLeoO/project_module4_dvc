package org.example.project_module4_dvc.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RelationshipResult {
    private boolean valid;
    private String message;
    private String detail;
    private String authorizedPersonName;

    public RelationshipResult(boolean valid, String message) {
        this.valid = valid;
        this.message = message;
    }
}
