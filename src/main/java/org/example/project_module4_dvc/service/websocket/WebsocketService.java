package org.example. project_module4_dvc. service.websocket;

import org.example.project_module4_dvc.dto.websocket. DossierItemDTO;
import org.example.project_module4_dvc.dto.websocket.DossierListUpdateMessage;
import org.example. project_module4_dvc. dto.websocket.DossierUpdateMessage;
import org.example.project_module4_dvc.dto.websocket.NotificationMessage;
import org.example.project_module4_dvc.entity. ops.OpsDossier;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class WebsocketService implements IWebsocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public WebsocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Broadcast notification to all officers in a department
     */
    @Override
    public void sendDepartmentNotification(String departmentName, NotificationMessage message) {
        messagingTemplate. convertAndSend(
                "/topic/department/" + departmentName,
                message
        );
    }

    /**
     * Send notification to specific user
     */
    @Override
    public void sendUserNotification(String username, NotificationMessage message) {
        messagingTemplate.convertAndSendToUser(
                username,
                "/queue/notifications",
                message
        );
    }

    /**
     * Broadcast urgent dossier alert to all officers
     */
    @Override
    public void broadcastUrgentDossier(String departmentName, Long dossierId, String dossierCode, int hoursLeft) {
        NotificationMessage message = NotificationMessage.builder()
                .type("URGENT_DOSSIER")
                .title("⚠️ Hồ sơ gấp!")
                .message("Hồ sơ " + dossierCode + " còn " + hoursLeft + " giờ nữa hết hạn!")
                .dossierId(dossierId)
                .dossierCode(dossierCode)
                .timestamp(LocalDateTime. now())
                .severity("DANGER")
                .build();

        sendDepartmentNotification(departmentName, message);
    }

    /**
     * Notify specialist when assigned to dossier
     */
    @Override
    public void notifyAssignment(String specialistUsername, Long dossierId, String dossierCode, String serviceName) {
        NotificationMessage message = NotificationMessage. builder()
                .type("ASSIGNMENT")
                .title("📋 Phân công mới")
                .message("Bạn được phân công xử lý hồ sơ " + dossierCode + " - " + serviceName)
                .dossierId(dossierId)
                .dossierCode(dossierCode)
                .timestamp(LocalDateTime.now())
                .severity("INFO")
                .build();

        sendUserNotification(specialistUsername, message);
    }

    /**
     * Notify citizen when dossier status changes
     */
    @Override
    public void notifyStatusChange(String citizenUsername, DossierUpdateMessage update) {
        NotificationMessage message = NotificationMessage.builder()
                .type("STATUS_UPDATE")
                .title("🔔 Cập nhật hồ sơ")
                .message("Hồ sơ " + update.getDossierCode() + " đã chuyển từ " +
                        update. getOldStatus() + " sang " + update.getNewStatus())
                .dossierId(update.getDossierId())
                .dossierCode(update. getDossierCode())
                .timestamp(LocalDateTime.now())
                .severity(getSeverityFromStatus(update.getNewStatus()))
                .build();

        sendUserNotification(citizenUsername, message);
    }

    /**
     * Broadcast new dossier to department
     */
    @Override
    public void broadcastNewDossier(String departmentName, Long dossierId, String dossierCode, String serviceName) {
        NotificationMessage message = NotificationMessage. builder()
                .type("NEW_DOSSIER")
                .title("📥 Hồ sơ mới")
                .message("Có hồ sơ mới:  " + dossierCode + " - " + serviceName)
                .dossierId(dossierId)
                .dossierCode(dossierCode)
                .timestamp(LocalDateTime.now())
                .severity("INFO")
                .build();

        sendDepartmentNotification(departmentName, message);
    }

    // ==================== 🔥 NEW METHODS FOR REAL-TIME LIST ====================

    /**
     * 🔥 Broadcast new dossier to department list (with full DTO)
     */
    @Override
    public void broadcastNewDossierToList(String departmentName, OpsDossier dossier) {
        DossierItemDTO dossierDTO = convertToDTO(dossier);

        DossierListUpdateMessage listMessage = DossierListUpdateMessage.builder()
                .action("ADD")
                .dossier(dossierDTO)
                .build();

        // Send to list channel
        messagingTemplate.convertAndSend(
                "/topic/department/" + departmentName + "/dossier-list",
                listMessage
        );

        // Also send notification toast
        NotificationMessage notification = NotificationMessage.builder()
                .type("NEW_DOSSIER")
                .title("📥 Hồ sơ mới")
                .message("Có hồ sơ mới: " + dossier.getDossierCode() + " - " +
                        dossier.getService().getServiceName())
                .dossierId(dossier. getId())
                .dossierCode(dossier.getDossierCode())
                .timestamp(LocalDateTime.now())
                .severity(dossierDTO.getIsUrgent() ? "WARNING" : "INFO")
                .build();

        sendDepartmentNotification(departmentName, notification);
    }

    /**
     * 🔥 Broadcast dossier update (status change, assignment)
     */
    @Override
    public void broadcastDossierUpdate(String departmentName, OpsDossier dossier) {
        DossierItemDTO dossierDTO = convertToDTO(dossier);

        DossierListUpdateMessage message = DossierListUpdateMessage. builder()
                .action("UPDATE")
                .dossier(dossierDTO)
                .build();

        messagingTemplate.convertAndSend(
                "/topic/department/" + departmentName + "/dossier-list",
                message
        );
    }

    /**
     * 🔥 Broadcast dossier removal from list (completed/rejected)
     */
    @Override
    public void broadcastDossierRemoval(String departmentName, Long dossierId, String dossierCode) {
        DossierItemDTO dossierDTO = DossierItemDTO.builder()
                .id(dossierId)
                .dossierCode(dossierCode)
                .build();

        DossierListUpdateMessage message = DossierListUpdateMessage.builder()
                .action("REMOVE")
                .dossier(dossierDTO)
                .build();

        messagingTemplate.convertAndSend(
                "/topic/department/" + departmentName + "/dossier-list",
                message
        );
    }

    /**
     * 🔥 Send to specialist's personal list
     */
    @Override
    public void sendToSpecialistList(String username, OpsDossier dossier, String action) {
        DossierItemDTO dossierDTO = convertToDTO(dossier);

        DossierListUpdateMessage message = DossierListUpdateMessage.builder()
                .action(action) // ADD, UPDATE, REMOVE
                . dossier(dossierDTO)
                .build();

        messagingTemplate.convertAndSendToUser(
                username,
                "/queue/dossier-list",
                message
        );
    }

    // ==================== HELPER METHODS ====================

    /**
     * Convert OpsDossier entity to DossierItemDTO
     */
    private DossierItemDTO convertToDTO(OpsDossier dossier) {
        Integer hoursLeft = null;
        Boolean isUrgent = false;

        if (dossier.getDueDate() != null) {
            long hours = Duration.between(LocalDateTime.now(), dossier.getDueDate()).toHours();
            hoursLeft = (int) hours;
            isUrgent = hours <= 6 && hours > 0;
        }

        return DossierItemDTO.builder()
                .id(dossier.getId())
                .dossierCode(dossier.getDossierCode())
                .serviceName(dossier.getService().getServiceName())
                .applicantFullName(dossier.getApplicant().getFullName())
                .dossierStatus(dossier.getDossierStatus())
                .submissionDate(dossier.getSubmissionDate())
                .dueDate(dossier.getDueDate())
                .hoursLeft(hoursLeft)
                .currentHandlerName(dossier.getCurrentHandler() != null ?
                        dossier.getCurrentHandler().getFullName() : null)
                .isUrgent(isUrgent)
                .build();
    }

    /**
     * Get severity color from status
     */
    private String getSeverityFromStatus(String status) {
        return switch (status) {
            case "APPROVED" -> "SUCCESS";
            case "REJECTED" -> "DANGER";
            case "NEED_SUPPLEMENT" -> "WARNING";
            default -> "INFO";
        };
    }
}