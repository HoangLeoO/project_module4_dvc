package org.example.project_module4_dvc.service.websocket;

import org.example.project_module4_dvc.dto.websocket.DossierUpdateMessage;
import org.example.project_module4_dvc.dto.websocket. NotificationMessage;
import org.example.project_module4_dvc.entity.ops.OpsDossier;
import org. springframework.messaging.simp.SimpMessagingTemplate;
import org. springframework.stereotype.Service;

import java.time.LocalDateTime;


public interface IWebsocketService {



    void sendDepartmentNotification(String departmentName, NotificationMessage message);
    void sendUserNotification(String username, NotificationMessage message);
    void broadcastUrgentDossier(String departmentName, Long dossierId, String dossierCode, int hoursLeft);
    void notifyAssignment(String specialistUsername, Long dossierId, String dossierCode, String serviceName);
    void notifyStatusChange(String citizenUsername, DossierUpdateMessage update);
    void broadcastNewDossier(String departmentName, Long dossierId, String dossierCode, String serviceName);

    // 🔥 New methods for real-time list updates
    void broadcastNewDossierToList(String departmentName, OpsDossier dossier);
    void broadcastDossierUpdate(String departmentName, OpsDossier dossier);
    void broadcastDossierRemoval(String departmentName, Long dossierId, String dossierCode);
    void sendToSpecialistList(String username, OpsDossier dossier, String action);
}
