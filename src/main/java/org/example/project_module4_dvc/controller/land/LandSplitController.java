package org.example.project_module4_dvc.controller.land;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.project_module4_dvc.dto.landConversion.*;
import org.example.project_module4_dvc.dto.formData.LandSplitMergeFormDTO;
import org.example.project_module4_dvc.entity.cat.CatService;
import org.example.project_module4_dvc.entity.ops.OpsDossier;
import org.example.project_module4_dvc.entity.sys.SysUser;
import org.example.project_module4_dvc.repository.cat.CatServiceRepository;
import org.example.project_module4_dvc.repository.sys.SysDepartmentRepository;
import org.example.project_module4_dvc.repository.sys.SysUserRepository;
import org.example.project_module4_dvc.service.FileStorageService;
import org.example.project_module4_dvc.service.learder.LandConversionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/citizen/land")
public class LandSplitController {

    @Autowired
    private SysUserRepository userRepository;

    @Autowired
    private SysDepartmentRepository deptRepository;

    @Autowired
    private CatServiceRepository catServiceRepository;

    @Autowired
    private LandConversionService landConversionService;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Trang nộp hồ sơ tách thửa đất
     * GET /citizen/land/submit-split
     */
    @GetMapping("/submit-split")
    public String showSubmitSplitForm(Model model, Principal principal) {
        SysUser user = userRepository.findByUsername(principal.getName());
        if (user.getCitizen() != null) {
            model.addAttribute("cccd", user.getCitizen().getCccd());
            model.addAttribute("address", user.getCitizen().getPermanentAddress());
        } else {
            model.addAttribute("cccd", user.getUsername());
            model.addAttribute("address", "Chưa cập nhật");
        }

        // Load danh sách đơn vị tiếp nhận
        model.addAttribute("depts", deptRepository.findAllByLevel(2));

        // Load thông tin lệ phí
        CatService service = catServiceRepository.findByServiceCode("DD03_TACHHOP").orElse(null);
        if (service != null) {
            model.addAttribute("fee", service.getFeeAmount());
        } else {
            model.addAttribute("fee", BigDecimal.ZERO);
        }

        // Phí cấp Giấy chứng nhận (Tạm tính)
        BigDecimal feeGCN = new BigDecimal(150000);
        model.addAttribute("feeGCN", feeGCN);

        // Tổng phí = Phí thẩm định + Phí GCN
        BigDecimal fee = (service != null && service.getFeeAmount() != null) ? service.getFeeAmount()
                : BigDecimal.ZERO;
        model.addAttribute("totalFee", fee.add(feeGCN));

        return "pages/land/submit-land-split-merge";
    }

    /**
     * Xử lý nộp hồ sơ tách thửa
     * POST /citizen/land/submit-split
     */
    @PostMapping("/submit-split")
    @ResponseBody
    public ResponseEntity<?> submitSplitDossier(
            @RequestParam("serviceCode") String serviceCode,
            @RequestParam("currentOwner") String currentOwner,
            @RequestParam("ownerIdNumber") String ownerIdNumber,
            @RequestParam("ownerAddress") String ownerAddress,
            @RequestParam("receivingDeptId") Long receivingDeptId,
            @RequestParam("landCertificateNumber") String landCertificateNumber,
            @RequestParam("landPlotNumber") String landPlotNumber,
            @RequestParam("mapSheetNumber") String mapSheetNumber,
            @RequestParam("originalAreaM2") String originalAreaM2,
            @RequestParam("numberOfNewPlots") Integer numberOfNewPlots,
            @RequestParam("splitAreas") String splitAreas, // "60.0,60.0"
            @RequestParam("splitReason") String splitReason,
            @RequestParam(value = "surveyCompleted", defaultValue = "false") Boolean surveyCompleted,
            @RequestParam(value = "files", required = false) List<MultipartFile> files,
            Principal principal) {

        try {
            SysUser currentUser = userRepository.findByUsername(principal.getName());

            // Tạo DTO form data
            LandSplitMergeFormDTO formDTO = new LandSplitMergeFormDTO();
            formDTO.setCurrentOwner(currentOwner);
            formDTO.setOwnerIdNumber(ownerIdNumber);
            formDTO.setOwnerAddress(ownerAddress);
            formDTO.setLandCertificateNumber(landCertificateNumber);
            formDTO.setLandPlotNumber(landPlotNumber);
            formDTO.setMapSheetNumber(mapSheetNumber);

            // Convert originalAreaM2 to BigDecimal
            try {
                formDTO.setOriginalAreaM2(new BigDecimal(originalAreaM2));
            } catch (Exception e) {
                formDTO.setOriginalAreaM2(null);
            }

            formDTO.setNumberOfNewPlots(numberOfNewPlots);

            // Parse split areas "60.0,60.0" thành List<BigDecimal>
            List<BigDecimal> areasList = new ArrayList<>();
            if (splitAreas != null && !splitAreas.trim().isEmpty()) {
                String[] parts = splitAreas.split(",");
                for (String part : parts) {
                    try {
                        areasList.add(new BigDecimal(part.trim()));
                    } catch (Exception e) {
                        // Skip invalid values
                    }
                }
            }
            formDTO.setRequestedSplitAreas(areasList);

            formDTO.setSplitReason(splitReason);
            formDTO.setSurveyCompleted(surveyCompleted);
            // setServiceCode is not in DTO, handled separately in logic or redundant if not
            // needed in form data object specifically for logic,
            // but we can put it in a specific field if we extend, for now mapping to JSON.

            // Serialize DTO to JSON
            String formDataJson = objectMapper.writeValueAsString(formDTO);

            // Tạo DTO
            DossierSubmitDTO dto = new DossierSubmitDTO();

            // Tìm service theo serviceCode
            CatService service = catServiceRepository.findByServiceCode(serviceCode)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy dịch vụ với mã: " + serviceCode));
            dto.setServiceId(service.getId());

            dto.setApplicantId(currentUser.getId());
            dto.setReceivingDeptId(receivingDeptId);
            dto.setFormData(formDataJson);

            // Xử lý File Upload
            if (files != null && !files.isEmpty()) {
                List<FileUploadDTO> fileDTOs = new ArrayList<>();
                for (MultipartFile file : files) {
                    if (!file.isEmpty()) {
                        FileUploadDTO f = new FileUploadDTO();
                        f.setFileName(file.getOriginalFilename());
                        f.setFileType(file.getContentType());

                        // Lưu file thật và lấy URL
                        String fileUrl = fileStorageService.store(file);
                        f.setFileUrl(fileUrl);

                        fileDTOs.add(f);
                    }
                }
                dto.setFiles(fileDTOs);
            }

            // Submit hồ sơ
            OpsDossier dossier = landConversionService.submitDossier(dto);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Nộp hồ sơ thành công!");
            response.put("dossierId", dossier.getId());
            response.put("dossierCode", dossier.getDossierCode());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
