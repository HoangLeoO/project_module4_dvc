package org.example.project_module4_dvc.dto.websocket;

import lombok.*;

import java.time.LocalDateTime;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class DossierUpdateMessage {
    private Long dossierId;
    private String dossierCode;
    private String oldStatus;
    private String newStatus;
    private String handlerName;
    private String action;
    private String comment;
}
