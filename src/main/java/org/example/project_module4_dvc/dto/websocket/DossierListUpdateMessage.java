package org.example.project_module4_dvc.dto.websocket;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class DossierListUpdateMessage {
    private String action; // ADD, UPDATE, REMOVE
    private DossierItemDTO dossier;
}
