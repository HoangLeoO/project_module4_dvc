package org.example.project_module4_dvc.controller.land;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.project_module4_dvc.dto.landConversion.*;
import org.example.project_module4_dvc.dto.view.DossierDetailViewDTO;
import org.example.project_module4_dvc.entity.ops.OpsDossier;
import org.example.project_module4_dvc.entity.ops.OpsDossierLog;
import org.example.project_module4_dvc.entity.sys.SysUser;
import org.example.project_module4_dvc.repository.ops.OpsDossierLogRepository;
import org.example.project_module4_dvc.repository.ops.OpsDossierRepository;
import org.example.project_module4_dvc.repository.sys.SysUserRepository;
import org.example.project_module4_dvc.entity.cat.CatService;
import org.example.project_module4_dvc.repository.cat.CatServiceRepository;
import org.example.project_module4_dvc.repository.mock.MockLandRepository;
import org.example.project_module4_dvc.service.learder.LandConversionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
// @RequestMapping("/land") - Removed to split endpoints by security domain
public class LandDossierWebController {
    @Autowired
    private LandConversionService landConversionService;
    @Autowired
    private OpsDossierRepository dossierRepository;
    @Autowired
    private OpsDossierLogRepository logRepository;
    @Autowired
    private SysUserRepository userRepository;
    @Autowired
    private CatServiceRepository catServiceRepository;
    @Autowired
    private org.example.project_module4_dvc.repository.sys.SysDepartmentRepository deptRepository;
    @Autowired
    private MockLandRepository mockLandRepository;
    @Autowired
    private org.example.project_module4_dvc.repository.ops.OpsDossierFileRepository fileRepository;
    @Autowired
    private org.example.project_module4_dvc.service.FileStorageService fileStorageService;
    @Autowired
    private ObjectMapper objectMapper;

    // ==================== BƯỚC 1: NỘP HỒ SƠ (CITIZEN) ====================
    /**
     * Trang nộp hồ sơ chuyển mục đích sử dụng đất
     * GET /citizen/land/submit
     */
    @GetMapping("/citizen/land/submit")
    public String showSubmitForm(Model model, Principal principal) {
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
        CatService service = catServiceRepository.findByServiceCode("DD02_CHUYENMDSD").orElse(null);
        if (service != null) {
            model.addAttribute("fee", service.getFeeAmount());
        } else {
            model.addAttribute("fee", java.math.BigDecimal.ZERO);
        }

        // Phí cấp Giấy chứng nhận (Tạm tính)
        java.math.BigDecimal feeGCN = new java.math.BigDecimal(100000);
        model.addAttribute("feeGCN", feeGCN);

        // Tạm thu = Phí thẩm định + Phí GCN
        java.math.BigDecimal fee = (service != null && service.getFeeAmount() != null) ? service.getFeeAmount()
                : java.math.BigDecimal.ZERO;
        model.addAttribute("totalFee", fee.add(feeGCN));

        return "pages/land/submit-land-purpose-change";
    }

    /**
     * Lấy danh sách thửa đất của công dân hiện tại
     * GET /citizen/land/my-lands
     */
    @GetMapping("/citizen/land/my-lands")
    @ResponseBody
    public ResponseEntity<?> getMyLands(Principal principal) {
        try {
            SysUser user = userRepository.findByUsername(principal.getName());
            if (user.getCitizen() == null) {
                return ResponseEntity.ok(List.of());
            }
            return ResponseEntity.ok(mockLandRepository.findByOwnerId(user.getCitizen().getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Xử lý nộp hồ sơ
     * POST /citizen/land/submit
     */
    @PostMapping("/citizen/land/submit")
    @ResponseBody
    public ResponseEntity<?> submitDossier(
            @RequestParam("serviceCode") String serviceCode,
            @RequestParam("currentOwner") String currentOwner,
            @RequestParam("ownerIdNumber") String ownerIdNumber,
            @RequestParam("ownerAddress") String ownerAddress,
            @RequestParam("receivingDeptId") Long receivingDeptId,
            @RequestParam("landCertificateNumber") String landCertificateNumber,
            @RequestParam("landPlotNumber") String landPlotNumber,
            @RequestParam("landMapSheet") String landMapSheet,
            @RequestParam("landAddress") String landAddress,
            @RequestParam("landArea") String landArea,
            @RequestParam("landPurpose") String landPurpose,
            @RequestParam("newPurpose") String newPurpose,
            @RequestParam("changeReason") String changeReason,
            @RequestParam("commitment") String commitment,
            @RequestParam(value = "files", required = false) List<org.springframework.web.multipart.MultipartFile> files,
            Principal principal) {

        try {
            SysUser currentUser = userRepository.findByUsername(principal.getName());

            // Tạo formData JSON
            Map<String, Object> formDataMap = new java.util.HashMap<>();
            formDataMap.put("currentOwner", currentOwner);
            formDataMap.put("ownerIdNumber", ownerIdNumber);
            formDataMap.put("ownerAddress", ownerAddress);
            formDataMap.put("landCertificateNumber", landCertificateNumber);
            formDataMap.put("landPlotNumber", landPlotNumber);
            formDataMap.put("mapSheetNumber", landMapSheet);
            formDataMap.put("landAddress", landAddress);

            // Convert landArea to BigDecimal
            try {
                formDataMap.put("landAreaM2", new java.math.BigDecimal(landArea));
            } catch (Exception e) {
                formDataMap.put("landAreaM2", null);
            }

            // Map land purpose code to full text
            Map<String, String> purposeMap = new java.util.HashMap<>();
            purposeMap.put("ODT", "Đất ở tại đô thị (ODT)");
            purposeMap.put("ONT", "Đất ở tại nông thôn (ONT)");
            purposeMap.put("TMC", "Đất thương mại, dịch vụ (TMC)");
            purposeMap.put("SXK", "Đất sản xuất, kinh doanh phi nông nghiệp (SXK)");
            purposeMap.put("NKH", "Đất nông nghiệp khác (NKH)");
            purposeMap.put("CLN", "Đất trồng cây lâu năm (CLN)");
            purposeMap.put("BHK", "Đất trồng cây hàng năm khác (BHK)");

            String currentPurposeText = purposeMap.getOrDefault(landPurpose, landPurpose);
            String newPurposeText = purposeMap.getOrDefault(newPurpose, newPurpose);

            formDataMap.put("currentLandPurpose", currentPurposeText);
            formDataMap.put("requestedLandPurpose", newPurposeText);
            formDataMap.put("reasonForChange", changeReason);
            formDataMap.put("commitment", commitment);
            formDataMap.put("serviceCode", serviceCode);

            String formDataJson = objectMapper.writeValueAsString(formDataMap);

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
                List<FileUploadDTO> fileDTOs = new java.util.ArrayList<>();
                for (org.springframework.web.multipart.MultipartFile file : files) {
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

            // Gọi service
            OpsDossier result = landConversionService.submitDossier(dto);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Nộp hồ sơ thành công",
                    "dossierId", result.getId(),
                    "dossierCode", result.getDossierCode()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", e.getMessage()));
        }
    }

    // ==================== BƯỚC 2: TIẾP NHẬN (OFFICER) ====================
    /**
     * Trang danh sách hồ sơ đất cần tiếp nhận
     * GET /officer/land/reception
     */
    @GetMapping("/officer/land/reception")
    public String showReceptionList(Model model, Principal principal) {
        // Lấy hồ sơ đất đai với status = NEW (Sử dụng method optimized lazy load)
        List<OpsDossier> dossiers = dossierRepository
                .findWithRelationsByDossierStatusAndServiceServiceCodeStartingWith("NEW", "DD");

        model.addAttribute("dossiers", dossiers);
        return "pages/land/reception-list";
    }

    /**
     * Trang chi tiết tiếp nhận hồ sơ
     * GET /officer/land/reception/{id}
     */
    @GetMapping("/officer/land/reception/{id}")
    public String showReceptionDetail(@PathVariable Long id, Model model) {
        OpsDossier dossier = dossierRepository.findWithRelationsById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ"));

        // Lấy danh sách cán bộ địa chính để gán
        // TODO: Tạm thời lấy tất cả user, cần filter theo role sau
        List<SysUser> landOfficers = userRepository.findAll().stream()
                .filter(u -> "OFFICIAL".equals(u.getUserType()))
                .toList();

        // Create View DTO
        DossierDetailViewDTO viewDto = new DossierDetailViewDTO(dossier);

        // Load files
        List<org.example.project_module4_dvc.entity.ops.OpsDossierFile> files = fileRepository
                .findOpsDossierFileByDossier_Id(id);
        viewDto.setFiles(files);

        model.addAttribute("dossier", viewDto);
        model.addAttribute("landOfficers", landOfficers);
        model.addAttribute("files", files);

        // --- GENERIC VIEW SUPPORT ---
        model.addAttribute("schema", dossier.getService().getFormSchema());
        model.addAttribute("data", dossier.getFormData());
        model.addAttribute("formFragment", "fragments/dynamic-form-readonly :: render");

        return "pages/officer/officer-reception";
    }

    /**
     * Xử lý tiếp nhận hồ sơ
     * POST /officer/land/reception/submit
     */
    @PostMapping("/officer/land/reception/submit")
    public String submitReception(
            @RequestParam Long dossierId,
            @RequestParam String action, // ACCEPT, REJECT, REQUIRE_SUPPLEMENT
            @RequestParam(required = false) Long nextHandlerId,
            @RequestParam String comments,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        try {
            SysUser currentUser = userRepository.findByUsername(principal.getName());

            DossierReceiveDTO dto = new DossierReceiveDTO();
            dto.setDossierId(dossierId);
            dto.setHandlerId(currentUser.getId());
            dto.setAction(action);
            dto.setNextHandlerId(nextHandlerId);
            dto.setComments(comments);

            landConversionService.receiveDossier(dto);

            redirectAttributes.addFlashAttribute("message", "Tiếp nhận hồ sơ thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }

        return "redirect:/officer/land/reception";
    }

    // ==================== BƯỚC 3: THẨM ĐỊNH ====================
    /**
     * Trang danh sách hồ sơ cần thẩm định
     * GET /land/appraisal
     */
    @GetMapping("/specialist/land/appraisal")
    public String showAppraisalList(Model model, Principal principal) {
        SysUser currentUser = userRepository.findByUsername(principal.getName());

        // Lấy hồ sơ đất được gán cho user hiện tại với status = PROCESSING
        List<OpsDossier> dossiers = dossierRepository
                .findByCurrentHandlerIdAndDossierStatusAndServiceServiceCodeStartingWith(
                        currentUser.getId(), "PROCESSING", "DD");

        model.addAttribute("dossiers", dossiers);
        return "pages/land/appraisal-list";
    }

    /**
     * Trang chi tiết thẩm định
     * GET /land/appraisal/{id}
     */
    @GetMapping("/specialist/land/appraisal/{id}")
    public String showAppraisalDetail(@PathVariable Long id, Model model) {
        OpsDossier dossier = dossierRepository.findWithRelationsById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ"));

        // Create View DTO
        DossierDetailViewDTO viewDto = new DossierDetailViewDTO(dossier);
        // Load files
        List<org.example.project_module4_dvc.entity.ops.OpsDossierFile> files = fileRepository
                .findOpsDossierFileByDossier_Id(id);
        viewDto.setFiles(files);

        model.addAttribute("dossier", viewDto);
        model.addAttribute("files", files);

        // Add Generic Support for Appraisal too (in case template is updated)
        model.addAttribute("schema", dossier.getService().getFormSchema());
        model.addAttribute("data", dossier.getFormData());
        model.addAttribute("formFragment", "fragments/dynamic-form-readonly :: render");

        return "pages/land/appraisal-detail";
    }

    /**
     * Xử lý thẩm định
     * POST /land/appraisal/submit
     */
    @PostMapping("/specialist/land/appraisal/submit")
    public String submitAppraisal(
            @RequestParam Long dossierId,
            @RequestParam String result, // PASS, FAIL
            @RequestParam String comments,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        try {
            SysUser currentUser = userRepository.findByUsername(principal.getName());

            DossierAppraisalDTO dto = new DossierAppraisalDTO();
            dto.setDossierId(dossierId);
            dto.setOfficerId(currentUser.getId());
            dto.setResult(result);
            dto.setComments(comments);

            landConversionService.appraiseDossier(dto);

            redirectAttributes.addFlashAttribute("message", "Thẩm định thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }

        return "redirect:/specialist/land/appraisal";
    }

    // ==================== BƯỚC 4: TRÌNH LÃNH ĐẠO ====================
    /**
     * Trang trình lãnh đạo
     * GET /land/submit-leader/{id}
     */
    @GetMapping("/specialist/land/submit-leader/{id}")
    public String showSubmitLeaderForm(@PathVariable Long id, Model model) {
        OpsDossier dossier = dossierRepository.findWithRelationsById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ"));

        // Lấy danh sách chủ tịch
        // TODO: Tạm thời lấy tất cả user, cần filter theo role LEADER sau
        List<SysUser> chairmen = userRepository.findAll().stream()
                .filter(u -> "OFFICIAL".equals(u.getUserType()))
                .toList();

        model.addAttribute("dossier", dossier);
        model.addAttribute("chairmen", chairmen);

        return "pages/land/submit-leader";
    }

    /**
     * Xử lý trình lãnh đạo
     * POST /land/submit-leader/submit
     */
    @PostMapping("/specialist/land/submit-leader/submit")
    public String submitToLeader(
            @RequestParam Long dossierId,
            @RequestParam Long chairmanId,
            @RequestParam String comments,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        try {
            SysUser currentUser = userRepository.findByUsername(principal.getName());

            landConversionService.submitToLeader(
                    dossierId, currentUser.getId(), chairmanId, comments);

            redirectAttributes.addFlashAttribute("message", "Trình lãnh đạo thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }

        return "redirect:/specialist/land/appraisal";
    }

    // ==================== BƯỚC 5: PHÊ DUYỆT ====================
    /**
     * Trang danh sách hồ sơ cần phê duyệt (cho Chủ tịch)
     * GET /land/approval
     */
    @GetMapping("/leader/land/approval")
    public String showApprovalList(Model model, Principal principal) {
        SysUser currentUser = userRepository.findByUsername(principal.getName());

        // Lấy hồ sơ đất được gán cho chủ tịch với status = PROCESSING
        List<OpsDossier> dossiers = dossierRepository
                .findWithRelationsByCurrentHandlerIdAndDossierStatusAndServiceServiceCodeStartingWith(
                        currentUser.getId(), "PROCESSING", "DD");

        model.addAttribute("dossiers", dossiers);
        return "pages/land/approval-list";
    }

    /**
     * Trang chi tiết phê duyệt
     * GET /land/approval/{id}
     */
    @GetMapping("/leader/land/approval/{id}")
    public String showApprovalDetail(@PathVariable Long id, Model model) {
        OpsDossier dossier = dossierRepository.findWithRelationsById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ"));

        // Lấy lịch sử xử lý
        List<OpsDossierLog> logs = logRepository.findByDossierIdOrderByCreatedAtAsc(id);

        model.addAttribute("dossier", dossier);
        model.addAttribute("logs", logs);

        return "pages/land/approval-detail";
    }

    /**
     * Xử lý phê duyệt
     * POST /land/approval/submit
     */
    @PostMapping("/leader/land/approval/submit")
    public String submitApproval(
            @RequestParam Long dossierId,
            @RequestParam String decision, // APPROVE, REJECT
            @RequestParam String comments,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        try {
            SysUser currentUser = userRepository.findByUsername(principal.getName());

            DossierApprovalDTO dto = new DossierApprovalDTO();
            dto.setDossierId(dossierId);
            dto.setChairmanId(currentUser.getId());
            dto.setDecision(decision);
            dto.setComments(comments);

            landConversionService.chairmanApprove(dto);

            redirectAttributes.addFlashAttribute("message", "Phê duyệt thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }

        return "redirect:/leader/land/approval";
    }

    // ==================== BƯỚC 6A: CẬP NHẬT SỔ ĐẤT ====================
    /**
     * Trang danh sách hồ sơ cần cập nhật sổ
     * GET /land/update-record
     */
    @GetMapping("/specialist/land/update-record")
    public String showUpdateRecordList(Model model, Principal principal) {
        // Lấy hồ sơ đã được phê duyệt, chưa cập nhật sổ
        // (Kiểm tra log xem đã có action APPROVE nhưng chưa có UPDATE_LAND_DB)
        List<OpsDossier> dossiers = dossierRepository
                .findWithRelationsByDossierStatusAndServiceServiceCodeStartingWith("PROCESSING", "DD");

        model.addAttribute("dossiers", dossiers);
        return "pages/land/update-record-list";
    }

    /**
     * Trang cập nhật sổ đất
     * GET /land/update-record/{id}
     */
    @GetMapping("/specialist/land/update-record/{id}")
    public String showUpdateRecordForm(@PathVariable Long id, Model model) {
        OpsDossier dossier = dossierRepository.findWithRelationsById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ"));

        model.addAttribute("dossier", dossier);

        return "pages/land/update-record";
    }

    /**
     * Xử lý cập nhật sổ
     * POST /land/update-record/submit
     */
    @PostMapping("/specialist/land/update-record/submit")
    public String submitUpdateRecord(
            @RequestParam Long dossierId,
            @RequestParam String comments,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        try {
            SysUser currentUser = userRepository.findByUsername(principal.getName());

            landConversionService.updateLandRecord(
                    dossierId, currentUser.getId(), comments);

            redirectAttributes.addFlashAttribute("message", "Cập nhật sổ đất thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }

        return "redirect:/specialist/land/update-record";
    }

    // ==================== BƯỚC 6B: HOÀN TẤT ====================
    /**
     * Trang danh sách hồ sơ cần hoàn tất
     * GET /land/finish
     */
    @GetMapping("/officer/land/finish")
    public String showFinishList(Model model) {
        // Lấy hồ sơ đã cập nhật sổ, chưa hoàn tất
        List<OpsDossier> dossiers = dossierRepository
                .findByDossierStatusAndServiceServiceCodeStartingWith("PROCESSING", "DD");

        model.addAttribute("dossiers", dossiers);
        return "pages/land/finish-list";
    }

    /**
     * Xử lý hoàn tất hồ sơ
     * POST /land/finish/submit
     */
    @PostMapping("/officer/land/finish/submit")
    public String submitFinish(
            @RequestParam Long dossierId,
            @RequestParam String comments,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        try {
            SysUser currentUser = userRepository.findByUsername(principal.getName());

            landConversionService.finishDossier(
                    dossierId, currentUser.getId(), comments);

            redirectAttributes.addFlashAttribute("message", "Hoàn tất hồ sơ thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }

        return "redirect:/officer/land/finish";
    }

    // ==================== HELPER: XEM LỊCH SỬ ====================
    /**
     * Trang xem lịch sử xử lý hồ sơ
     * GET /land/history/{id}
     */

    // Map cho tất cả các role có thể xem history
    @GetMapping({ "/officer/land/history/{id}", "/specialist/land/history/{id}", "/leader/land/history/{id}" })
    public String showHistory(@PathVariable Long id, Model model) {
        OpsDossier dossier = dossierRepository.findWithRelationsById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ"));

        List<OpsDossierLog> logs = logRepository.findByDossierIdOrderByCreatedAtAsc(id);

        model.addAttribute("dossier", dossier);
        model.addAttribute("logs", logs);

        return "pages/land/history";
    }

}