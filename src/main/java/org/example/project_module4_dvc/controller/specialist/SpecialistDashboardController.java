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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigInteger;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/specialist/dashboard")
public class SpecialistDashboardController {

    private final SpecialistService specialistService_1;
    private final IOfficerService officerService;

    private final FileStorageService fileStorageService;
    private final ISpecialistService specialistService;

    private final IOpsDossierFileService opsDossierFileService;

    public SpecialistDashboardController(IOfficerService officerService, FileStorageService fileStorageService,
            ISpecialistService specialistService, IOpsDossierFileService opsDossierFileService,
            SpecialistService specialistService_1) {
        this.officerService = officerService;
        this.fileStorageService = fileStorageService;
        this.specialistService = specialistService;
        this.opsDossierFileService = opsDossierFileService;
        this.specialistService_1 = specialistService_1;
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
    public String updateDossierStatus(@RequestParam("dossierId") Long id) {
        NewDossierDTO newDossierDTO = officerService.findById(id);
        specialistService.updateDossierStatus(id, "VERIFIED", Long.valueOf(2), newDossierDTO.getDueDate(), "");
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
}
