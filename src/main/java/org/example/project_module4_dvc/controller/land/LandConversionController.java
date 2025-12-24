package org.example.project_module4_dvc.controller.land;
import org.example.project_module4_dvc.dto.landConversion.*;
import org.example.project_module4_dvc.entity.ops.OpsDossier;
import org.example.project_module4_dvc.service.learder.LandConversionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController
@RequestMapping("/api/land")
public class LandConversionController {
    @Autowired
    private LandConversionService landConversionService;
    /**
     * Bước 2: Tiếp nhận hồ sơ
     * POST /api/land/receive
     */
    @PostMapping("/receive")
    public ResponseEntity<?> receiveDossier(@RequestBody DossierReceiveDTO dto) {
        try {
            OpsDossier result = landConversionService.receiveDossier(dto);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Tiếp nhận hồ sơ thành công",
                    "dossier", result
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }
    /**
     * Bước 3: Thẩm định nhu cầu
     * POST /api/land/appraise
     */
    @PostMapping("/appraise")
    public ResponseEntity<?> appraiseDossier(@RequestBody DossierAppraisalDTO dto) {
        try {
            OpsDossier result = landConversionService.appraiseDossier(dto);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Thẩm định thành công",
                    "dossier", result
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }
    /**
     * Bước 4: Trình lãnh đạo
     * POST /api/land/submit-to-leader
     */
    @PostMapping("/submit-to-leader")
    public ResponseEntity<?> submitToLeader(@RequestBody Map<String, Object> request) {
        try {
            Long dossierId = Long.parseLong(request.get("dossierId").toString());
            Long officerId = Long.parseLong(request.get("officerId").toString());
            Long chairmanId = Long.parseLong(request.get("chairmanId").toString());
            String comments = (String) request.get("comments");
            OpsDossier result = landConversionService.submitToLeader(
                    dossierId, officerId, chairmanId, comments);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Trình lãnh đạo thành công",
                    "dossier", result
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }
    /**
     * Bước 5: Chủ tịch phê duyệt
     * POST /api/land/approve
     */
    @PostMapping("/approve")
    public ResponseEntity<?> chairmanApprove(@RequestBody DossierApprovalDTO dto) {
        try {
            OpsDossier result = landConversionService.chairmanApprove(dto);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Phê duyệt thành công",
                    "dossier", result
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }
    /**
     * Bước 6A: Cập nhật sổ đất
     * POST /api/land/update-record
     */
    @PostMapping("/update-record")
    public ResponseEntity<?> updateLandRecord(@RequestBody Map<String, Object> request) {
        try {
            Long dossierId = Long.parseLong(request.get("dossierId").toString());
            Long officerId = Long.parseLong(request.get("officerId").toString());
            String comments = (String) request.get("comments");
            OpsDossier result = landConversionService.updateLandRecord(
                    dossierId, officerId, comments);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Cập nhật sổ đất thành công",
                    "dossier", result
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }
    /**
     * Bước 6B: Hoàn tất hồ sơ
     * POST /api/land/finish
     */
    @PostMapping("/finish")
    public ResponseEntity<?> finishDossier(@RequestBody Map<String, Object> request) {
        try {
            Long dossierId = Long.parseLong(request.get("dossierId").toString());
            Long handlerId = Long.parseLong(request.get("handlerId").toString());
            String comments = (String) request.get("comments");
            OpsDossier result = landConversionService.finishDossier(
                    dossierId, handlerId, comments);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Hoàn tất hồ sơ thành công",
                    "dossier", result
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }
}