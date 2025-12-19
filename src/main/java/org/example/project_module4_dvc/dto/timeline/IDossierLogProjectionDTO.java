package org.example.project_module4_dvc.dto.timeline;

import java.time.LocalDateTime;

public interface IDossierLogProjectionDTO {
    LocalDateTime getCreatedAt();
    String getComments();
    String getActorName();
    String getDeptName();
}
