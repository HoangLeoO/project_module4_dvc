package org.example.project_module4_dvc.service.learder;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.project_module4_dvc.entity.ops.OpsDossier;
import org.example.project_module4_dvc.entity.ops.OpsDossierFile;
import org.example.project_module4_dvc.repository.sys.SysDepartmentRepository;
import org.example.project_module4_dvc.service.FileStorageService;
import org.springframework.transaction.annotation.Transactional;
import org.example.project_module4_dvc.dto.dossier.NewDossierDTO;
import org.example.project_module4_dvc.entity.cat.CatService;
import org.example.project_module4_dvc.entity.sys.SysDepartment;
import org.example.project_module4_dvc.entity.sys.SysUser;
import org.example.project_module4_dvc.repository.cat.CatServiceRepository;
import org.example.project_module4_dvc.repository.ops.OpsDossierFileRepository;
import org.example.project_module4_dvc.repository.ops.OpsDossierLogRepository;
import org.example.project_module4_dvc.repository.ops.OpsDossierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TOpsDossierService implements ITOpsDossierService {

    @Autowired
    private OpsDossierRepository dossierRepository;
    @Autowired
    private OpsDossierLogRepository dossierLogRepository;
    @Autowired
    private OpsDossierFileRepository opsDossierFileRepository;
    @Autowired
    private CatServiceRepository catServiceRepository;
    @Autowired
    private SysDepartmentRepository departmentRepository;
    @Autowired
    private org.example.project_module4_dvc.repository.ops.OpsLogWorkflowStepRepository opsLogWorkflowStepRepository;
    @Autowired
    private org.example.project_module4_dvc.repository.cat.CatWorkflowStepRepository catWorkflowStepRepository;
    @Autowired
    private ObjectMapper objectMapper; // Dùng để convert DTO -> JSON String
    @Autowired
    private FileStorageService fileStorageService; // Service lưu file vật lý

    // ================================================================
    // ÁNH XẠ LOGIC SQL VÀO JAVA
    // ================================================================
    @Transactional(rollbackFor = Exception.class) // Đảm bảo cả 2 bảng được lưu thành công hoặc rollback toàn bộ
    // @Transactional
    public void submitDossier(NewDossierDTO request, List<MultipartFile> files, SysUser currentUser) throws Exception {

        // 1. Lấy thông tin dịch vụ (Tương ứng SELECT id FROM cat_services...)

        Long serviceId = Long.parseLong(request.getServiceId().toString());
        CatService service = catServiceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Dịch vụ không tồn tại"));

        // ================================================================
        // BƯỚC 1: TẠO HỒ SƠ MỚI (ops_dossiers)
        // ================================================================
        OpsDossier dossier = new OpsDossier();
        dossier.setService(service);
        dossier.setApplicant(currentUser); // applicant_id

        // Tạo mã hồ sơ: HS-MãDV-Năm-UUID
        // Tạo mã hồ sơ: HS-MãDV-Năm-UUID (có kiểm tra trùng lặp)
        String dossierCode;
        do {
            dossierCode = "HS-" + service.getServiceCode() + "-" + LocalDateTime.now().getYear() + "-"
                    + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        } while (dossierRepository.existsByDossierCode(dossierCode));
        dossier.setDossierCode(dossierCode);

        dossier.setSubmissionDate(LocalDateTime.now());

        // Tính hạn xử lý: NOW() + SLA (DATE_ADD)
        dossier.setDueDate(LocalDateTime.now().plusHours(service.getSlaHours()));

        // --- FIX: Set Receiving Department ---
        SysDepartment receivingDept = null;
        if (request.getReceivingDeptId() != null) {
            receivingDept = departmentRepository.findById(request.getReceivingDeptId()).orElse(null);
        }

        if (receivingDept == null) {
            // Fallback default
            receivingDept = departmentRepository.findByDeptCode("WARD-001");
        }

        if (receivingDept == null) {
            throw new RuntimeException(
                    "Không tìm thấy đơn vị tiếp nhận (Mã: " + request.getReceivingDeptId() + " hoặc Default WARD-001)");
        }
        dossier.setReceivingDept(receivingDept);

        // Convert Form DTO sang JSON String (Cột form_data)
        // String jsonFormData = objectMapper.writeValueAsString(request.getFormData());
        dossier.setFormData(request.getFormData());

        // Lưu vào DB (Lúc này JPA tự động thực hiện LAST_INSERT_ID để lấy ID)
        dossier = dossierRepository.save(dossier);

        // Set dossierCode and id back to request DTO (for VNPay payment URL generation)
        request.setDossierCode(dossierCode);
        request.setId(dossier.getId());

        // ================================================================
        // BƯỚC 2: LƯU FILE (ops_dossier_files)
        // ================================================================
        // Xử lý File
        if (files != null) {
            for (MultipartFile file : files) {
                // 1. Gọi StorageService để lưu file vật lý và lấy đường dẫn
                String fileUrl = fileStorageService.store(file);

                // 2. Lưu thông tin vào DB (Bảng ops_dossier_files)
                OpsDossierFile fileEntity = new OpsDossierFile();
                fileEntity.setDossier(dossier);
                fileEntity.setFileName(file.getOriginalFilename());
                fileEntity.setFileType(file.getContentType());
                fileEntity.setFileUrl(fileUrl); // Lưu đường dẫn trả về từ store()

                opsDossierFileRepository.save(fileEntity);
            }
        }

        // ================================================================
        // BƯỚC 3: GHI LOG (ops_dossier_logs)
        // ================================================================
        // org.example.project_module4_dvc.entity.ops.OpsDossierLog log = new
        // org.example.project_module4_dvc.entity.ops.OpsDossierLog();
        // log.setDossier(dossier);
        // log.setActorId(currentUser.getId());
        // log.setAction("NOP_HO_SO");
        // log.setPrevStatus(null);
        // log.setNextStatus(dossier.getDossierStatus()); // NEW
        // log.setComments("Công dân nộp hồ sơ trực tuyến");
        // // log.setCreateDate(LocalDateTime.now());
        //
        // dossierLogRepository.save(log);

        // 3. Ghi Log + Workflow Step (Bước 1: Nộp hồ sơ)
        recordStepCompletion(dossier, currentUser.getId(), "NOP_HO_SO", null, "NEW", "Công dân nộp hồ sơ trực tuyến",
                1);
    }

    private void recordStepCompletion(OpsDossier dossier, Long actorId, String action, String prevStatus,
            String nextStatus, String comments, int stepOrder) {
        // Create Log
        org.example.project_module4_dvc.entity.ops.OpsDossierLog log = new org.example.project_module4_dvc.entity.ops.OpsDossierLog();
        log.setDossier(dossier);
        log.setActorId(actorId);
        log.setAction(action);
        log.setPrevStatus(prevStatus);
        log.setNextStatus(nextStatus);
        log.setComments(comments);
        org.example.project_module4_dvc.entity.ops.OpsDossierLog savedLog = dossierLogRepository.save(log);

        // Find Step for this service
        catWorkflowStepRepository.findAll().stream()
                .filter(s -> s.getService().getId().equals(dossier.getService().getId())
                        && s.getStepOrder() == stepOrder)
                .findFirst()
                .ifPresent(step -> {
                    org.example.project_module4_dvc.entity.ops.OpsLogWorkflowStep lws = new org.example.project_module4_dvc.entity.ops.OpsLogWorkflowStep();
                    lws.setLog(savedLog);
                    lws.setWorkflowStep(step);
                    lws.setDescription(step.getStepName() + " hoàn thành");
                    lws.setCreatedAt(java.time.Instant.now());
                    opsLogWorkflowStepRepository.save(lws);
                });
    }

    // Hàm phụ lấy đuôi file
    private String getFileExtension(String fileName) {
        if (fileName == null)
            return "UNKNOWN";
        int lastIndexOf = fileName.lastIndexOf(".");
        if (lastIndexOf == -1)
            return "UNKNOWN";
        return fileName.substring(lastIndexOf + 1).toUpperCase();
    }
}
