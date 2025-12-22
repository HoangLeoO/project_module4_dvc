package org.example.project_module4_dvc.service.learder;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.project_module4_dvc.entity.ops.OpsDossier;
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

    @Autowired private OpsDossierRepository dossierRepository;
    @Autowired private OpsDossierLogRepository logRepository;
    @Autowired private OpsDossierFileRepository fileRepository;
    @Autowired private CatServiceRepository catServiceRepository;
    @Autowired private org.example.project_module4_dvc.repository.sys.SysDepartmentRepository departmentRepository;
    @Autowired private ObjectMapper objectMapper; // Dùng để convert DTO -> JSON String
//    @Autowired private FileStorageService fileStorageService; // Service lưu file vật lý

    // ================================================================
    // ÁNH XẠ LOGIC SQL VÀO JAVA
    // ================================================================
//    @Transactional(rollbackFor = Exception.class) // Đảm bảo cả 3 bảng được lưu thành công hoặc rollback toàn bộ
    @Transactional
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
        String dossierCode = "HS-" + service.getServiceCode() + "-" + LocalDateTime.now().getYear() + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        dossier.setDossierCode(dossierCode);

        dossier.setSubmissionDate(LocalDateTime.now());

        // Tính hạn xử lý: NOW() + SLA (DATE_ADD)
        dossier.setDueDate(LocalDateTime.now().plusHours(service.getSlaHours()));

        // --- FIX: Set Receiving Department (Tạm thời hardcode WARD-001 - UBND Phường Hải Châu) ---
        // Trong thực tế, cần logic routing dựa vào địa chỉ công dân hoặc chọn từ form
        SysDepartment receivingDept = departmentRepository.findByDeptCode("WARD-001");
        if (receivingDept == null) {
            throw new RuntimeException("Không tìm thấy phòng ban tiếp nhận mặc định (WARD-001)");
        }
        dossier.setReceivingDept(receivingDept);

        // Convert Form DTO sang JSON String (Cột form_data)
        // String jsonFormData = objectMapper.writeValueAsString(request.getFormData());
        dossier.setFormData(request.getFormData());

        // Lưu vào DB (Lúc này JPA tự động thực hiện LAST_INSERT_ID để lấy ID)
        dossier = dossierRepository.save(dossier);



        // ================================================================
        // BƯỚC 2: LƯU FILE (ops_dossier_files)
        // ================================================================
//        if (files != null && !files.isEmpty()) {
//            for (MultipartFile file : files) {
//                // 3a. Lưu file vật lý vào ổ cứng/server
//                String fileUrl = fileStorageService.storeFile(file); // Hàm này trả về đường dẫn "/uploads/..."
//
//                // 3b. Lưu thông tin vào DB
//                OpsDossierFile fileEntity = new OpsDossierFile();
//                fileEntity.setDossier(dossier);
//                fileEntity.setFileName(file.getOriginalFilename());
//                fileEntity.setFileUrl(fileUrl); // Đường dẫn file
//                fileEntity.setFileType(getFileExtension(file.getOriginalFilename()));
//
//                fileRepository.save(fileEntity);
//            }
//        }
    }

    // Hàm phụ lấy đuôi file
    private String getFileExtension(String fileName) {
        if (fileName == null) return "UNKNOWN";
        int lastIndexOf = fileName.lastIndexOf(".");
        if (lastIndexOf == -1) return "UNKNOWN";
        return fileName.substring(lastIndexOf + 1).toUpperCase();
    }
}
