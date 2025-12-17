package org.example.project_module4_dvc.controller;

import lombok.RequiredArgsConstructor;
import org.example.project_module4_dvc.dto.OpsDossierDetailDTO;
import org.example.project_module4_dvc.dto.OpsDossierSummaryDTO;
import org.example.project_module4_dvc.service.OpsDossierService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller cho quản lý hồ sơ
 * Trả về DTO thay vì Entity để:
 * - Tránh vấn đề lazy loading
 * - Kiểm soát được dữ liệu trả về
 * - Tối ưu hiệu suất
 */
@RestController
@RequestMapping("/api/dossiers")
@RequiredArgsConstructor
public class OpsDossierController {

    private final OpsDossierService dossierService;

    /**
     * GET /api/dossiers/{id}
     * Lấy thông tin chi tiết hồ sơ từ nhiều bảng
     * 
     * Ví dụ response:
     * {
     * "dossierId": 1,
     * "dossierCode": "HS001",
     * "dossierStatus": "PROCESSING",
     * "applicantFullName": "Nguyễn Văn A",
     * "serviceName": "Đăng ký kinh doanh",
     * "handlerFullName": "Trần Thị B",
     * "handlerDeptName": "Phòng Đăng ký kinh doanh",
     * ...
     * }
     */
    @GetMapping("/{id}")
    public ResponseEntity<OpsDossierDetailDTO> getDossierDetail(@PathVariable Long id) {
        OpsDossierDetailDTO dossier = dossierService.getDossierDetail(id);
        return ResponseEntity.ok(dossier);
    }

    /**
     * GET /api/dossiers
     * Lấy danh sách tất cả hồ sơ (tóm tắt)
     */
    @GetMapping
    public ResponseEntity<List<OpsDossierSummaryDTO>> getAllDossiers() {
        List<OpsDossierSummaryDTO> dossiers = dossierService.getAllDossiers();
        return ResponseEntity.ok(dossiers);
    }

    /**
     * GET /api/dossiers/applicant/{applicantId}
     * Lấy danh sách hồ sơ của một người nộp
     */
    @GetMapping("/applicant/{applicantId}")
    public ResponseEntity<List<OpsDossierSummaryDTO>> getDossiersByApplicant(
            @PathVariable Long applicantId) {
        List<OpsDossierSummaryDTO> dossiers = dossierService.getDossiersByApplicant(applicantId);
        return ResponseEntity.ok(dossiers);
    }

    /**
     * GET /api/dossiers/handler/{handlerId}
     * Lấy danh sách hồ sơ được phân công cho một cán bộ
     */
    @GetMapping("/handler/{handlerId}")
    public ResponseEntity<List<OpsDossierSummaryDTO>> getDossiersByHandler(
            @PathVariable Long handlerId) {
        List<OpsDossierSummaryDTO> dossiers = dossierService.getDossiersByHandler(handlerId);
        return ResponseEntity.ok(dossiers);
    }

    /**
     * GET /api/dossiers/status/{status}
     * Lấy danh sách hồ sơ theo trạng thái
     * 
     * Ví dụ: GET /api/dossiers/status/NEW
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<OpsDossierSummaryDTO>> getDossiersByStatus(
            @PathVariable String status) {
        List<OpsDossierSummaryDTO> dossiers = dossierService.getDossiersByStatus(status);
        return ResponseEntity.ok(dossiers);
    }
}
