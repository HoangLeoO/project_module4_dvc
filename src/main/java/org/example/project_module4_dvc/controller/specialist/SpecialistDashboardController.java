package org.example.project_module4_dvc.controller.specialist;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.project_module4_dvc.config.CustomUserDetails;
import org.example.project_module4_dvc.controller.officer.DossierFormDataViewHelper;
import org.example.project_module4_dvc.dto.dossier.NewDossierDTO;
import org.example.project_module4_dvc.dto.formData.*;
import org.example.project_module4_dvc.dto.specialist.SpecialistAvailableDTO;
import org.example.project_module4_dvc.entity.ops.OpsDossierFile;
import org.example.project_module4_dvc.service.FileStorageService;
import org.example.project_module4_dvc.service.officer.IOfficerService;
import org.example.project_module4_dvc.service.ops.IOpsDossierFileService;
import org.example.project_module4_dvc.service.specialist.ISpecialistService;
import org.example.project_module4_dvc.service.specialist.SpecialistService;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.example.project_module4_dvc.entity.mock.MockCitizen;
import org.example.project_module4_dvc.repository.mock.MockCitizenRepository;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/specialist/dashboard")
public class SpecialistDashboardController {

    private final SpecialistService specialistService_1;
    private final IOfficerService officerService;

    private final FileStorageService fileStorageService;
    private final ISpecialistService specialistService;

    private final IOpsDossierFileService opsDossierFileService;
    private final MockCitizenRepository mockCitizenRepository;


    private SimpMessagingTemplate messagingTemplate;

    public SpecialistDashboardController(IOfficerService officerService, FileStorageService fileStorageService,
            ISpecialistService specialistService, IOpsDossierFileService opsDossierFileService,
            SpecialistService specialistService_1, MockCitizenRepository mockCitizenRepository) {
        this.officerService = officerService;
        this.fileStorageService = fileStorageService;
        this.specialistService = specialistService;
        this.opsDossierFileService = opsDossierFileService;
        this.specialistService_1 = specialistService_1;
        this.mockCitizenRepository = mockCitizenRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping("")
    public String getDossierList(Model model,
                                 @PageableDefault(size = 5, sort = "submissionDate", direction = Sort.Direction.ASC) Pageable pageable,
                                 @AuthenticationPrincipal CustomUserDetails userDetails) {
        model.addAttribute("officerName", userDetails.getFullName());
        model.addAttribute("specialistId", userDetails.getUserId());
        model.addAttribute("departmentName", userDetails.getDepartmentName());
        System.out.println(userDetails.getDepartmentName());
        Page<NewDossierDTO> page = specialistService.findAll("PENDING", userDetails.getDepartmentName(),
                userDetails.getUserId(), pageable);
        List<NewDossierDTO> nearDueList = specialistService.findNearlyDue(userDetails.getDepartmentName(),
                userDetails.getUserId());
        model.addAttribute("nearDueCount", nearDueList.size());
        model.addAttribute("dossiers", page);

        return "pages/specialist/specialist-dashboard";
    }

    @GetMapping("/reception")
    public String getReceptionForm(Model model, @RequestParam("id") Long id,
                                   @AuthenticationPrincipal CustomUserDetails userDetails) {
        model.addAttribute("officerName", userDetails.getFullName());
        model.addAttribute("departmentName", userDetails.getDepartmentName());
        List<OpsDossierFile> opsDossierFile = officerService.findFileByDossierId(id);
        List<SpecialistAvailableDTO> specialists = officerService.getAvailableSpecialists(id);
        NewDossierDTO newDossierDTO = officerService.findById(id);
        if (newDossierDTO == null) {
            // Redirect về trang danh sách và báo lỗi, hoặc trang 404
            return "redirect:/specialist/dashboard?error=dossier_not_found";
        }
        model.addAttribute("dossier", newDossierDTO);
        model.addAttribute("files", opsDossierFile);

        // Get service ID and set up form display
        BigInteger serviceId = newDossierDTO.getServiceId();
        Map<String, Object> formDataMap = newDossierDTO.getFormData();
        Object formDataDTO;

        try {
            switch (serviceId.intValue()) {
                case 1: // Khai sinh
                    formDataDTO = convertMapToDTO(formDataMap, BirthRegistrationFormDTO.class);
                    System.out.println("Processing Birth Registration");
                    break;
                case 2: // Kết hôn
                    formDataDTO = convertMapToDTO(formDataMap, MarriageRegistrationFormDTO.class);
                    System.out.println("Processing Marriage Registration");
                    break;
                case 3: // Khai tu
                    formDataDTO = convertMapToDTO(formDataMap, DeathRegistrationFormDTO.class);
                    System.out.println("Processing Land Change Registration");
                    break;
                case 4: // xac nhan tinh trang hon nhan
                    formDataDTO = convertMapToDTO(formDataMap, MaritalStatusCertificateFormDTO.class);
                    break;
                case 5: // thay doi quyen su dung dat
                    formDataDTO = convertMapToDTO(formDataMap, LandChangeRegistrationFormDTO.class);
                    break;
                case 6: // chuyen doi muc dich
                    formDataDTO = convertMapToDTO(formDataMap, LandPurposeChangeFormDTO.class);
                    break;
                case 7: // tach thua
                    formDataDTO = convertMapToDTO(formDataMap, LandSplitMergeFormDTO.class);
                    break;
                case 8: // Đăng ký kinh doanh hộ gia đình
                    formDataDTO = convertMapToDTO(formDataMap, HouseholdBusinessRegistrationFormDTO.class);
                    System.out.println("Processing Household Business Registration");
                    break;
                default:
                    System.err.println("Unknown service ID: " + serviceId);
                    formDataDTO = formDataMap;
            }
        } catch (Exception e) {
            System.err.println("Error processing service ID: " + e.getMessage());
            formDataDTO = formDataMap; // Fallback to Map
        }

        String fragmentPath = DossierFormDataViewHelper.getFormFragmentPath(serviceId);

        model.addAttribute("formData", formDataDTO);
        model.addAttribute("specialists", specialists);
        model.addAttribute("formFragment", fragmentPath);

        return "pages/specialist/specialist-reception";
    }

    @GetMapping("reception/update")
    public String updateDossierStatus(@RequestParam("dossierId") Long id, RedirectAttributes redirectAttributes) {
        NewDossierDTO newDossierDTO = officerService.findById(id);
        specialistService.updateDossierStatus(id, "VERIFIED", Long.valueOf(2), newDossierDTO.getDueDate(), "");
        redirectAttributes.addFlashAttribute("toastType", "success");
        redirectAttributes.addFlashAttribute("toastMessage", "Đã chuyển tiếp hồ sơ thành công!");
        return "redirect:/specialist/dashboard";
    }
    @PostMapping("reception/reject")
    public String rejectDossierStatus(@RequestParam("id") Long id, @RequestParam("reason") String reason, RedirectAttributes redirectAttributes) {
        officerService.updateDossierRejectStatus(id, "REJECTED", reason);
        redirectAttributes.addFlashAttribute("toastType", "success");
        redirectAttributes.addFlashAttribute("toastMessage", "Đã từ chối hồ sơ thành công!");
        return "redirect:/specialist/dashboard";
    }

    @GetMapping("/view-file/{id}")
    public ResponseEntity<Resource> viewFile(@PathVariable("id") Long id) {
        String filePath = opsDossierFileService.getById(id).getFileUrl();
        try {
            Resource resource = fileStorageService.loadFileAsResource(filePath);

            // Determine content type
            String contentType = "application/octet-stream";
            try {
                contentType = Files.probeContentType(resource.getFile().toPath());
            } catch (Exception e) {
                // ignore
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, contentType)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    private <T> T convertMapToDTO(Map<String, Object> dataMap, Class<T> dtoClass) {
        if (dataMap == null) {
            return null;
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        try {
            T dto = mapper.convertValue(dataMap, dtoClass);
            System.out.println("Successfully converted Map to " + dtoClass.getSimpleName());
            return dto;
        } catch (Exception e) {
            System.err.println("Error converting Map to " + dtoClass.getSimpleName() + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Kiểm tra tình trạng hôn nhân với CSDL dân cư
     * Dùng cho chuyên viên khi xử lý hồ sơ xác nhận tình trạng hôn nhân
     */
    @GetMapping("/verify-marital-status")
    @ResponseBody
    public ResponseEntity<?> verifyMaritalStatus(
            @RequestParam("idNumber") String idNumber,
            @RequestParam("declaredStatus") String declaredStatus) {
        try {
            // 1. Tìm công dân theo CCCD trong CSDL dân cư
            MockCitizen citizen = mockCitizenRepository.findByCccd(idNumber).orElse(null);

            Map<String, Object> response = new HashMap<>();

            if (citizen == null) {
                response.put("isMatch", false);
                response.put("declaredStatus", declaredStatus);
                response.put("actualStatus", "NOT_FOUND");
                response.put("message", "Không tìm thấy công dân trong CSDL với số CCCD: " + idNumber);
                return ResponseEntity.ok(response);
            }

            // 2. So sánh tình trạng hôn nhân (không phân biệt hoa thường)
            String actualStatus = citizen.getMaritalStatus() != null ? citizen.getMaritalStatus() : "UNKNOWN";
            boolean isMatch = actualStatus.equalsIgnoreCase(declaredStatus);

            // 3. Trả về kết quả
            response.put("isMatch", isMatch);
            response.put("declaredStatus", declaredStatus);
            response.put("actualStatus", actualStatus);
            response.put("citizenName", citizen.getFullName());

            if (isMatch) {
                response.put("message", "Thông tin tình trạng hôn nhân trùng khớp với CSDL dân cư.");
            } else {
                response.put("message", "Thông tin khai báo không khớp! Công dân khai "
                        + getStatusLabel(declaredStatus) + " nhưng CSDL ghi nhận " + getStatusLabel(actualStatus)
                        + ".");
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("isMatch", false);
            errorResponse.put("message", "Lỗi khi kiểm tra: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Chuyển đổi mã trạng thái sang nhãn tiếng Việt
     */
    private String getStatusLabel(String status) {
        if (status == null)
            return "Không xác định";
        switch (status.toUpperCase()) {
            case "SINGLE":
                return "Chưa kết hôn";
            case "MARRIED":
                return "Đã kết hôn";
            case "DIVORCED":
                return "Đã ly hôn";
            case "WIDOWED":
                return "Góa";
            case "NOT_FOUND":
                return "Không tìm thấy";
            default:
                return status;
        }
    }
}
