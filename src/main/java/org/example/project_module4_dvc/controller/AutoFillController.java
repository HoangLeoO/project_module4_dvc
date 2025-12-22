package org.example.project_module4_dvc.controller;

import org.example.project_module4_dvc.config.CustomUserDetails;
import org.example.project_module4_dvc.dto.RelationshipResult;
import org.example.project_module4_dvc.entity.mock.MockCitizen;
import org.example.project_module4_dvc.service.autoFill.AutoFillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/autofill")
public class AutoFillController {

    @Autowired
    private AutoFillService autoFillService;

    @GetMapping("/citizen/{cccd}")
    public ResponseEntity<?> getCitizenByCccd(@PathVariable String cccd) {
        MockCitizen citizen = autoFillService.getCitizenByCccd(cccd);
        if (citizen != null) {
            return ResponseEntity.ok(citizen);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/land/{cccd}")
    public ResponseEntity<?> getLandsByOwnerCccd(@PathVariable String cccd) {
        java.util.List<org.example.project_module4_dvc.entity.mock.MockLand> lands = autoFillService.getLandsByOwnerCccd(cccd);
        if (lands != null && !lands.isEmpty()) {
            return ResponseEntity.ok(lands);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyInfo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails)) {
             return ResponseEntity.status(401).build();
        }
        
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Long citizenId = userDetails.getCitizenId();
        
        if (citizenId == null) {
             return ResponseEntity.badRequest().body("Tài khoản chưa liên kết với công dân.");
        }
        
        MockCitizen citizen = autoFillService.getCitizenById(citizenId);
        return ResponseEntity.ok(citizen);
    }

    @GetMapping("/relation")
    public ResponseEntity<?> checkRelationship(@RequestParam String targetCccd) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails)) {
             return ResponseEntity.status(401).build();
        }
        
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Long citizenId = userDetails.getCitizenId();
        
        if (citizenId == null) {
             // Return a valid result object indicating failure due to missing profile
             return ResponseEntity.ok(new RelationshipResult(false, "Tài khoản của bạn chưa liên kết với dữ liệu dân cư. Vui lòng cập nhật hồ sơ!"));
        }
        
        RelationshipResult result = autoFillService.checkHouseholdRelationship(citizenId, targetCccd);
        return ResponseEntity.ok(result);
    }
}
