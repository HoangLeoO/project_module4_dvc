package org.example.project_module4_dvc.controller.leader;

import org.example.project_module4_dvc.config.CustomUserDetails;
import org.example.project_module4_dvc.dto.dossier.NewDossierDTO;
import org.example.project_module4_dvc.entity.cat.CatService;
import org.example.project_module4_dvc.entity.mock.MockCitizen;
import org.example.project_module4_dvc.entity.ops.OpsDossier;
import org.example.project_module4_dvc.entity.sys.SysUser;
import org.example.project_module4_dvc.repository.cat.CatServiceRepository;
import org.example.project_module4_dvc.service.autoFill.AutoFillService;
import org.example.project_module4_dvc.service.learder.ITOpsDossierService;
import org.example.project_module4_dvc.service.sys.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/citizen/")
public class PortalController {

    @Autowired
    private AutoFillService autoFillService;

    @Autowired
    private ISysUserService sysUserService;


    @Autowired
    private ITOpsDossierService dossierService;

//    @Autowired
//    private SysUserRepository sysUserRepository;

    @GetMapping("service/death/{serviceCode}")
    public String death(Model mode, @PathVariable String serviceCode) {
        OpsDossier opsDossier = new OpsDossier();
        
        // Auto-fill from logged-in user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails) {
           CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
            Long citizenId = userDetails.getCitizenId();
            if (citizenId != null) {
               MockCitizen citizen = autoFillService.getCitizenById(citizenId);
                if (citizen != null) {
                  Map<String, Object> formData = new HashMap<>();
                    formData.put("relativeFullName", citizen.getFullName());
                    formData.put("relativeIdNumber", citizen.getCccd());
                    if (citizen.getDob() != null) {
                        formData.put("relativeDateOfBirth", DateTimeFormatter.ofPattern("dd/MM/yyyy").format(citizen.getDob()));
                    }
                    formData.put("relativePhoneNumber","123456789"); // citizen.getPhoneNumber() does not exist in MockCitizen
                    formData.put("relativeAddress", citizen.getPermanentAddress());
                    
                    opsDossier.setFormData(formData);
                }
            }
        }
        
        mode.addAttribute("opsDossier", opsDossier);
        mode.addAttribute("serviceCode", serviceCode);
        return "pages/portal/portal-submit-death";
    }


    @GetMapping("service/land/{serviceCode}")
    public String landHL(Model mode, @PathVariable String serviceCode) {
        OpsDossier opsDossier = new OpsDossier();
        
        // Auto-fill from logged-in user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
            Long citizenId = userDetails.getCitizenId();
            if (citizenId != null) {
                MockCitizen citizen = autoFillService.getCitizenById(citizenId);
                if (citizen != null) {
                    Map<String, Object> formData = new HashMap<>();
                    formData.put("currentOwner", citizen.getFullName());
                    formData.put("ownerIdNumber", citizen.getCccd());
                    formData.put("ownerAddress", citizen.getPermanentAddress());
                    
                    opsDossier.setFormData(formData);
                }
            }
        }
        
        mode.addAttribute("opsDossier", opsDossier);
        mode.addAttribute("serviceCode", serviceCode);
        return "pages/portal/portal-submit-land-hl";
    }


    @Autowired
    private CatServiceRepository catServiceRepository;

    @PostMapping("submit")
    @ResponseBody
    public ResponseEntity<?> submitDossier(
            @RequestParam Map<String, String> allParams,
            @RequestParam(value = "files", required = false) List<MultipartFile> files,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            if (userDetails == null) {
                return ResponseEntity.status(401).body("Vui lòng đăng nhập");
            }

            // 1. Prepare DTO
           NewDossierDTO dto = new NewDossierDTO();
            
            // Extract Service ID
            String serviceIdStr = allParams.get("serviceId");
           BigInteger serviceId = null;

            if (serviceIdStr != null && !serviceIdStr.isEmpty()) {
                serviceId = new BigInteger(serviceIdStr);
            } else {
                // Try serviceCode
                String serviceCode = allParams.get("serviceCode");
                if (serviceCode != null && !serviceCode.isEmpty()) {
                   CatService service = catServiceRepository.findByServiceCode(serviceCode)
                            .orElse(null);
                     if (service != null) {
                         serviceId = java.math.BigInteger.valueOf(service.getId());
                     }
                }
            }

            if (serviceId == null) {
                return ResponseEntity.badRequest().body("Không tìm thấy thông tin Dịch vụ (serviceId hoặc serviceCode)");
            }
            
            dto.setServiceId(serviceId);

            // Extract Form Data (Remove system params)
            Map<String, Object> formData = new HashMap<>(allParams);
            formData.remove("serviceId");
            formData.remove("serviceCode");
            formData.remove("_csrf"); // If present
            dto.setFormData(formData);

            // 2. Get Current User Entity
            SysUser currentUser = sysUserService.findById(userDetails.getUserId());

            // 3. Call Service
            dossierService.submitDossier(dto, files, currentUser);

            org.example.project_module4_dvc.entity.cat.CatService serviceObj = catServiceRepository.findById(serviceId.longValue()).orElse(null);
            String serviceCodeForRedirect = (serviceObj != null) ? serviceObj.getServiceCode() : "";

            return ResponseEntity.ok(Map.of(
                "message", "Nộp hồ sơ thành công", 
                "status", "success",
                "serviceCode", serviceCodeForRedirect
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("message", "Lỗi: " + e.getMessage()));
        }
    }
}
