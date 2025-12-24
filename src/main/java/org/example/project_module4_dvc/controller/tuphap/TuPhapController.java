package org.example.project_module4_dvc.controller.tuphap;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.project_module4_dvc.config.CustomUserDetails;
import org.example.project_module4_dvc.entity.cat.CatService;
import org.example.project_module4_dvc.entity.mock.MockCitizen;
import org.example.project_module4_dvc.entity.ops.OpsDossier;
import org.example.project_module4_dvc.entity.ops.OpsDossierFile;
import org.example.project_module4_dvc.entity.sys.SysDepartment;
import org.example.project_module4_dvc.entity.sys.SysUser;
import org.example.project_module4_dvc.repository.cat.CatServiceRepository;
import org.example.project_module4_dvc.repository.mock.MockCitizenRepository;
import org.example.project_module4_dvc.repository.ops.OpsDossierFileRepository;
import org.example.project_module4_dvc.repository.ops.OpsDossierRepository;
import org.example.project_module4_dvc.repository.sys.SysDepartmentRepository;
import org.example.project_module4_dvc.repository.sys.SysUserRepository;
import org.example.project_module4_dvc.service.FileStorageService;
import org.example.project_module4_dvc.service.cat.ICatServiceService;
import org.example.project_module4_dvc.service.mock.IMockCitizenService;
import org.example.project_module4_dvc.service.sys.SysDepartmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Controller cho các thủ tục Tư pháp (Hộ tịch)
 * - Giấy xác nhận tình trạng hôn nhân
 */
@Controller
@RequestMapping("/citizen/tuphap")
public class TuPhapController {

    private ICatServiceService catServiceService;
    private IMockCitizenService mockCitizenService;
    private SysDepartmentService sysDepartmentService;
    private CatServiceRepository catServiceRepository;
    private SysUserRepository sysUserRepository;
    private SysDepartmentRepository sysDepartmentRepository;
    private OpsDossierRepository opsDossierRepository;
    private OpsDossierFileRepository opsDossierFileRepository;
    private FileStorageService fileStorageService;
    private ObjectMapper objectMapper;
    private MockCitizenRepository mockCitizenRepository;

    public TuPhapController(ICatServiceService catServiceService, IMockCitizenService mockCitizenService,
            SysDepartmentService sysDepartmentService, CatServiceRepository catServiceRepository,
            SysUserRepository sysUserRepository, SysDepartmentRepository sysDepartmentRepository,
            OpsDossierRepository opsDossierRepository, OpsDossierFileRepository opsDossierFileRepository,
            FileStorageService fileStorageService, ObjectMapper objectMapper,
            MockCitizenRepository mockCitizenRepository) {
        this.catServiceService = catServiceService;
        this.mockCitizenService = mockCitizenService;
        this.sysDepartmentService = sysDepartmentService;
        this.catServiceRepository = catServiceRepository;
        this.sysUserRepository = sysUserRepository;
        this.sysDepartmentRepository = sysDepartmentRepository;
        this.opsDossierRepository = opsDossierRepository;
        this.opsDossierFileRepository = opsDossierFileRepository;
        this.fileStorageService = fileStorageService;
        this.objectMapper = objectMapper;
        this.mockCitizenRepository = mockCitizenRepository;
    }

    /**
     * Hiển thị form đăng ký Giấy xác nhận tình trạng hôn nhân
     */
    @GetMapping("/marital-status")
    public String showMaritalStatusForm(
            @RequestParam(value = "code", required = false, defaultValue = "HT02_XACNHANHN") String code,
            Model model,
            Authentication authentication) {

        // Lấy thông tin dịch vụ
        var service = catServiceService.getServiceByCode(code);
        model.addAttribute("s", service);

        // Lấy thông tin người đăng nhập
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long citizenId = userDetails.getCitizenId();

        // Lấy thông tin công dân từ database
        MockCitizen citizen = mockCitizenService.findById(citizenId);
        model.addAttribute("citizen", citizen);

        // Load danh sách đơn vị hành chính
        model.addAttribute("sysDepartments", sysDepartmentService.getAll());
        model.addAttribute("activePage", "services");

        return "pages/portal/tuphap/marital-status-certificate";
    }

    /**
     * Xử lý submit hồ sơ Giấy xác nhận tình trạng hôn nhân
     */
    @PostMapping("/submit")
    @Transactional(rollbackFor = Exception.class)
    @ResponseBody
    public ResponseEntity<?> submitMaritalStatusCertificate(
            @RequestParam("serviceId") Long serviceId,
            @RequestParam("serviceCode") String serviceCode,
            @RequestParam("receivingDeptId") Long receivingDeptId,
            @RequestParam("requesterFullName") String requesterFullName,
            @RequestParam("dateOfBirth") String dateOfBirth,
            @RequestParam("idNumber") String idNumber,
            @RequestParam("currentMaritalStatus") String currentMaritalStatus,
            @RequestParam("confirmationPeriod") String confirmationPeriod,
            @RequestParam("purposeOfUse") String purposeOfUse,
            @RequestParam(value = "files", required = false) List<MultipartFile> files,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        try {
            // 1. Lấy thông tin Service
            CatService service = catServiceRepository.findById(serviceId)
                    .orElseThrow(() -> new RuntimeException("Dịch vụ không tồn tại"));

            // 2. Lấy thông tin User
            SysUser applicant = sysUserRepository.findById(userDetails.getUserId())
                    .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

            // 3. Lấy đơn vị tiếp nhận
            SysDepartment receivingDept = sysDepartmentRepository.findById(receivingDeptId)
                    .orElseThrow(() -> new RuntimeException("Đơn vị tiếp nhận không tồn tại"));

            // 4. Tạo hồ sơ mới
            OpsDossier dossier = new OpsDossier();
            dossier.setService(service);
            dossier.setApplicant(applicant);

            // Tạo mã hồ sơ: HS-{serviceCode}-{year}-{UUID}
            String dossierCode = "HS-" + serviceCode + "-" + LocalDateTime.now().getYear() + "-"
                    + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
            dossier.setDossierCode(dossierCode);

            dossier.setSubmissionDate(LocalDateTime.now());
            dossier.setDueDate(LocalDateTime.now().plusHours(service.getSlaHours()));
            dossier.setReceivingDept(receivingDept);

            // 5. Tạo formData JSON
            Map<String, Object> formData = new HashMap<>();
            formData.put("requesterFullName", requesterFullName);
            formData.put("dateOfBirth", dateOfBirth);
            formData.put("idNumber", idNumber);
            formData.put("currentMaritalStatus", currentMaritalStatus);
            formData.put("confirmationPeriod", confirmationPeriod);
            formData.put("purposeOfUse", purposeOfUse);

            dossier.setFormData(formData);

            // 6. Lưu hồ sơ
            dossier = opsDossierRepository.save(dossier);

            // 7. Lưu files
            if (files != null && !files.isEmpty()) {
                for (MultipartFile file : files) {
                    if (!file.isEmpty()) {
                        String fileUrl = fileStorageService.store(file);

                        OpsDossierFile fileEntity = new OpsDossierFile();
                        fileEntity.setDossier(dossier);
                        fileEntity.setFileName(file.getOriginalFilename());
                        fileEntity.setFileType(file.getContentType());
                        fileEntity.setFileUrl(fileUrl);

                        opsDossierFileRepository.save(fileEntity);
                    }
                }
            }

            // 8. Trả về kết quả thành công
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Nộp hồ sơ thành công!");
            response.put("dossierCode", dossierCode);
            response.put("dossierId", dossier.getId());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

}
