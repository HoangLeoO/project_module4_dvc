package org.example.project_module4_dvc.controller.officer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.project_module4_dvc.config.CustomUserDetails;
import org.example.project_module4_dvc.dto.dossier.NewDossierDTO;
import org.example.project_module4_dvc.dto.formData.*;
import org.example.project_module4_dvc.entity.ops.OpsDossierFile;
import org.example.project_module4_dvc.service.officer.IOfficerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/officer/dashboard")
public class DashboardController {
    private final IOfficerService officerService;


    public DashboardController(IOfficerService officerService) {
        this.officerService = officerService;
    }

    @GetMapping("")
    public String getDossierList(Model model, @PageableDefault(size = 5,
                                         sort = "submissionDate",
                                         direction = Sort.Direction.ASC) Pageable pageable,
                                 @AuthenticationPrincipal CustomUserDetails userDetails) {
        model.addAttribute("officerName", userDetails.getFullName());
        model.addAttribute("departmentName", userDetails.getDepartmentName());
        System.out.println(userDetails.getDepartmentName());
        Page<NewDossierDTO> page = officerService.findAll("NEW", userDetails.getDepartmentName(), pageable);
        List<NewDossierDTO> nearDueList = officerService.findNearlyDue(userDetails.getDepartmentName());
        model.addAttribute("nearDueCount", nearDueList.size());
        model.addAttribute("dossiers", page);


        return "pages/officer/officer-dashboard";
    }

    @GetMapping("/reception")
    public String getReceptionForm(Model model, @RequestParam("id") Long id) {
        List<OpsDossierFile> opsDossierFile = officerService.findFileByDossierId(id);

        NewDossierDTO newDossierDTO = officerService.findById(id);
        if (newDossierDTO == null) {
            // Redirect về trang danh sách và báo lỗi, hoặc trang 404
            return "redirect:/officer/dashboard?error=dossier_not_found";
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
                case 3: // Thay đổi đất đai
                    formDataDTO = convertMapToDTO(formDataMap, LandChangeRegistrationFormDTO.class);
                    System.out.println("Processing Land Change Registration");
                    break;
                case 4: // Đăng ký cư trú
                    formDataDTO = convertMapToDTO(formDataMap, ResidenceRegistrationFormDTO.class);
                    System.out.println("Processing Residence Registration");
                    break;
                case 5: // Thay đổi mục đích sử dụng đất
                    formDataDTO = convertMapToDTO(formDataMap, LandPurposeChangeFormDTO.class);
                    System.out.println("Processing Land Purpose Change");
                    break;
                case 6: // Tách/hợp nhất thửa đất
                    formDataDTO = convertMapToDTO(formDataMap, LandSplitMergeFormDTO.class);
                    System.out.println("Processing Land Split/Merge");
                    break;
                case 7: // Đăng ký tử vong
                    formDataDTO = convertMapToDTO(formDataMap, DeathRegistrationFormDTO.class);
                    System.out.println("Processing Death Registration");
                    break;
                case 8: // Đăng ký kinh doanh hộ gia đình
                    formDataDTO = convertMapToDTO(formDataMap, HouseholdBusinessRegistrationFormDTO.class);
                    System.out.println("Processing Household Business Registration");
                    break;
                case 9: // Giấy chứng nhận tình trạng hôn nhân
                    formDataDTO = convertMapToDTO(formDataMap, MaritalStatusCertificateFormDTO.class);
                    System.out.println("Processing Marital Status Certificate");
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
        model.addAttribute("formFragment", fragmentPath);

        return "pages/officer/officer-reception";
    }

    //pdf


    @GetMapping("reception/update")
    public String updateDossierStatus(@RequestParam("id") Long id) {
        officerService.updateDossierStatus(id, "PENDING", "");
        return "redirect:/official/dashboard";
    }

    @PostMapping("reception/reject")
    public String rejectDossierStatus(@RequestParam("id") Long id, @RequestParam("reason") String reason) {
        officerService.updateDossierStatus(id, "REJECTED", reason);
        return "redirect:/official/dashboard";
    }

    /**
     * Convert Map<String, Object> to appropriate DTO class
     */
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
}
