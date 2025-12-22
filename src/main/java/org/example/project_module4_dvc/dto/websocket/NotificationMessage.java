package org.example.project_module4_dvc.dto.websocket;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class NotificationMessage {
    private String type; // NEW_DOSSIER, URGENT_DOSSIER, STATUS_UPDATE, ASSIGNMENT
    private String title;
    private String message;
    private Long dossierId;
    private String dossierCode;
    private LocalDateTime timestamp;
    private String severity; // INFO, WARNING, DANGER, SUCCESS
}
