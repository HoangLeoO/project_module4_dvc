package org.example.project_module4_dvc.controller.officer;

import org.example.project_module4_dvc.entity.ops.OpsDossier;
import org.example.project_module4_dvc.repository.ops.OpsDossierRepository;
import org.example.project_module4_dvc.service.websocket.IWebsocketService;
import org.springframework.http.ResponseEntity;
import org. springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test/websocket")
public class WebSocketTestController {

    private final IWebsocketService websocketService;
    private final OpsDossierRepository opsDossierRepository;

    public WebSocketTestController(IWebsocketService websocketService,
                                   OpsDossierRepository opsDossierRepository) {
        this.websocketService = websocketService;
        this.opsDossierRepository = opsDossierRepository;
    }

    /**
     * Test broadcast new dossier
     * GET /api/test/websocket/new-dossier/1
     */
    @GetMapping("/new-dossier/{dossierId}")
    public ResponseEntity<String> testNewDossier(@PathVariable Long dossierId) {
        OpsDossier dossier = opsDossierRepository.findById(dossierId)
                .orElseThrow(() -> new RuntimeException("Not found"));

        websocketService.broadcastNewDossierToList(
                dossier.getReceivingDept().getDeptName(),
                dossier
        );

        return ResponseEntity.ok("✅ Broadcasted new dossier to list!");
    }

    /**
     * Test update dossier
     * GET /api/test/websocket/update-dossier/1
     */
    @GetMapping("/update-dossier/{dossierId}")
    public ResponseEntity<String> testUpdateDossier(@PathVariable Long dossierId) {
        OpsDossier dossier = opsDossierRepository. findById(dossierId)
                .orElseThrow(() -> new RuntimeException("Not found"));

        websocketService. broadcastDossierUpdate(
                dossier.getReceivingDept().getDeptName(),
                dossier
        );

        return ResponseEntity. ok("✅ Broadcasted dossier update!");
    }

    /**
     * Test remove dossier
     * GET /api/test/websocket/remove-dossier/1
     */
    @GetMapping("/remove-dossier/{dossierId}")
    public ResponseEntity<String> testRemoveDossier(@PathVariable Long dossierId) {
        OpsDossier dossier = opsDossierRepository.findById(dossierId)
                .orElseThrow(() -> new RuntimeException("Not found"));

        websocketService.broadcastDossierRemoval(
                dossier.getReceivingDept().getDeptName(),
                dossier. getId(),
                dossier. getDossierCode()
        );

        return ResponseEntity. ok("✅ Broadcasted dossier removal!");
    }

    /**
     * Test urgent alert
     * GET /api/test/websocket/urgent? dept=Phường Hải Châu
     */
    @GetMapping("/urgent")
    public ResponseEntity<String> testUrgent(@RequestParam String dept) {
        websocketService.broadcastUrgentDossier(dept, 999L, "HS-TEST-999", 3);
        return ResponseEntity.ok("✅ Sent urgent alert to " + dept);
    }
}