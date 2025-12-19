package org.example.project_module4_dvc.dto.OpsDossierDTO;

public interface CitizenNotificationProjection {
    Long getDossierId();
    String getDossierCode();
    String getAction();
    String getMessage();
    java.time.LocalDateTime getCreatedAt();
}
