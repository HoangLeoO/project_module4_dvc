package org.example.project_module4_dvc.controller.dossier;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.project_module4_dvc.config.CustomUserDetails;
import org.example.project_module4_dvc.dto.dossier.NewDossierDTO;
import org.example.project_module4_dvc.dto.formData.DeathRegistrationFormDTO;
import org.example.project_module4_dvc.dto.formData.LandChangeRegistrationFormDTO;
import org.example.project_module4_dvc.entity.cat.CatService;
import org.example.project_module4_dvc.entity.mock.MockCitizen;
import org.example.project_module4_dvc.entity.ops.OpsDossier;
import org.example.project_module4_dvc.entity.sys.SysUser;
import org.example.project_module4_dvc.repository.cat.CatServiceRepository;
import org.example.project_module4_dvc.service.autoFill.AutoFillService;
import org.example.project_module4_dvc.service.learder.ITOpsDossierService;
import org.example.project_module4_dvc.service.sys.ISysUserService;
import org.example.project_module4_dvc.service.sys.SysDepartmentService;
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

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CatServiceRepository catServiceRepository;

    @Autowired
    private SysDepartmentService sysDepartmentService;


    @GetMapping("services/death/{serviceCode}")
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
                        formData.put("relativeDateOfBirth",
                                DateTimeFormatter.ofPattern("dd/MM/yyyy").format(citizen.getDob()));
                    }
                    formData.put("relativePhoneNumber","123456789"); // citizen.getPhoneNumber() does not exist in MockCitizen
                    formData.put("relativeAddress", citizen.getPermanentAddress());
                    
                    opsDossier.setFormData(formData);
                }
            }
        }
        
        mode.addAttribute("opsDossier", opsDossier);
        mode.addAttribute("serviceCode", serviceCode);
        mode.addAttribute("sysDepartment",sysDepartmentService.getAllByLevel(2));
        return "pages/portal/portal-submit-death";
    }


    @GetMapping("services/land/{serviceCode}")
    public String landHL(Model mode, @PathVariable String serviceCode) {
        OpsDossier opsDossier = new OpsDossier();

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
        mode.addAttribute("sysDepartment", sysDepartmentService.getAll());
        return "pages/portal/portal-submit-land-hl";
    }

    @PostMapping("submit")
    @ResponseBody
    public ResponseEntity<?> submitDossier(
            @RequestParam Map<String, String> allParams,
            @RequestParam(value = "files", required = false) List<MultipartFile> files,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            if (userDetails == null) {
                return ResponseEntity.status(401).body(Map.of("status", "error", "message", "Vui lòng đăng nhập"));
            }

            String serviceIdStr = allParams.get("serviceId");
            String serviceCode = allParams.get("serviceCode");

            CatService service = null;
            if (serviceIdStr != null && !serviceIdStr.isEmpty()) {
                service = catServiceRepository.findById(Long.parseLong(serviceIdStr)).orElse(null);
            } else if (serviceCode != null && !serviceCode.isEmpty()) {
                service = catServiceRepository.findByServiceCode(serviceCode).orElse(null);
            }

            if (service == null) {
                return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "Không tìm thấy thông tin Dịch vụ (serviceId hoặc serviceCode)"));
            }
            
            NewDossierDTO dto = new NewDossierDTO();
            dto.setServiceId(BigInteger.valueOf(service.getId()));

            // Extract receivingDeptId
            String receivingDeptIdStr = allParams.get("receivingDeptId");
            if (receivingDeptIdStr != null && !receivingDeptIdStr.isEmpty()) {
                dto.setReceivingDeptId(Long.parseLong(receivingDeptIdStr));
            }

            objectMapper.registerModule(new JavaTimeModule());
            Map<String, String> rawData = new HashMap<>(allParams);
            rawData.remove("serviceId");
            rawData.remove("serviceCode");
            rawData.remove("receivingDeptId");
            rawData.remove("_csrf");

            Object formDto = null;

            if (service.getServiceCode().contains("HS-HK") || service.getServiceName().contains("Khai tử")) {
                formDto = objectMapper.convertValue(rawData, DeathRegistrationFormDTO.class);
            } else if (service.getServiceCode().contains("HS-DD") || service.getServiceName().contains("đất đai")) {
                formDto = objectMapper.convertValue(rawData, LandChangeRegistrationFormDTO.class);
            } else if (service.getServiceCode().contains("HS-KS") || service.getServiceName().contains("Khai sinh")) {
                formDto = objectMapper.convertValue(rawData,
                        org.example.project_module4_dvc.dto.formData.BirthRegistrationFormDTO.class);
            } else {
                formDto = rawData;
            }

            Map<String, Object> cleanFormData = objectMapper.convertValue(formDto, Map.class);
            dto.setFormData(cleanFormData);

            SysUser currentUser = sysUserService.findById(userDetails.getUserId());

            dossierService.submitDossier(dto, files, currentUser);

            return ResponseEntity.ok(Map.of(
                    "message", "Nộp hồ sơ thành công",
                    "status", "success",
                    "serviceCode", service.getServiceCode()));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("message", "Lỗi: " + e.getMessage()));
        }
    }
}
