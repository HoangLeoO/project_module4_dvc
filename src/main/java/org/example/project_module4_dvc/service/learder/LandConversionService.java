package org.example.project_module4_dvc.service.learder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.project_module4_dvc.dto.landConversion.DossierAppraisalDTO;
import org.example.project_module4_dvc.dto.landConversion.DossierApprovalDTO;
import org.example.project_module4_dvc.dto.landConversion.DossierReceiveDTO;
import org.example.project_module4_dvc.dto.landConversion.DossierSubmitDTO;
import org.example.project_module4_dvc.entity.cat.CatService;
import org.example.project_module4_dvc.entity.cat.CatWorkflowStep;
import org.example.project_module4_dvc.entity.ops.OpsDossier;
import org.example.project_module4_dvc.entity.ops.OpsDossierLog;
import org.example.project_module4_dvc.entity.ops.OpsLogWorkflowStep;
import org.example.project_module4_dvc.entity.sys.SysDepartment;
import org.example.project_module4_dvc.entity.sys.SysUser;
import org.example.project_module4_dvc.repository.cat.CatServiceRepository;
import org.example.project_module4_dvc.repository.cat.CatWorkflowStepRepository;
import org.example.project_module4_dvc.repository.ops.OpsDossierLogRepository;
import org.example.project_module4_dvc.repository.ops.OpsDossierRepository;
import org.example.project_module4_dvc.repository.ops.OpsLogWorkflowStepRepository;
import org.example.project_module4_dvc.repository.sys.SysUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class LandConversionService {
        @Autowired
        private OpsDossierRepository opsDossierRepository;
        @Autowired
        private OpsDossierLogRepository logRepo;
        @Autowired
        private OpsLogWorkflowStepRepository workflowStepRepo;
        @Autowired
        private CatServiceRepository serviceRepo;
        @Autowired
        private SysUserRepository userRepo;
        @Autowired
        private CatWorkflowStepRepository catWorkflowRepo;

        // ==================== BƯỚC 1: CÔNG DÂN NỘP HỒ SƠ ====================
        @Transactional
        public OpsDossier submitDossier(DossierSubmitDTO dto) {
                // Tạo mã hồ sơ
                String dossierCode = "HS_" + LocalDateTime.now()
                                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

                // Lấy thông tin dịch vụ để tính SLA
                CatService service = serviceRepo.findById(dto.getServiceId())
                                .orElseThrow(() -> new RuntimeException("Service not found"));

                LocalDateTime dueDate = LocalDateTime.now()
                                .plusHours(service.getSlaHours());

                // Tạo hồ sơ mới
                OpsDossier dossier = new OpsDossier();
                dossier.setDossierCode(dossierCode);
                dossier.setService(service);
                dossier.setReceivingDept(new SysDepartment());
                dossier.getReceivingDept().setId(dto.getReceivingDeptId());
                dossier.setApplicant(new SysUser());
                dossier.getApplicant().setId(dto.getApplicantId());
                dossier.setDossierStatus("NEW");
                dossier.setSubmissionDate(LocalDateTime.now());
                dossier.setDueDate(dueDate);

                // Parse formData từ String sang Map
                try {
                        ObjectMapper mapper = new ObjectMapper();
                        Map<String, Object> formDataMap = mapper.readValue(dto.getFormData(),
                                        new TypeReference<Map<String, Object>>() {
                                        });
                        dossier.setFormData(formDataMap);
                } catch (Exception e) {
                        throw new RuntimeException("Lỗi parse formData: " + e.getMessage());
                }

                dossier = opsDossierRepository.save(dossier);

                // Lưu log
                createLog(dossier, dto.getApplicantId(), "SUBMIT",
                                null, "NEW", "Người dân nộp hồ sơ");

                return dossier;
        }

        // ==================== BƯỚC 2: MỘT CỬA TIẾP NHẬN ====================
        @Transactional
        public OpsDossier receiveDossier(DossierReceiveDTO dto) {
                OpsDossier dossier = opsDossierRepository.findById(dto.getDossierId())
                                .orElseThrow(() -> new RuntimeException("Dossier not found"));

                String prevStatus = dossier.getDossierStatus();
                String action = dto.getAction();
                String nextStatus = prevStatus;

                if ("ACCEPT".equals(action)) {
                        // Chấp nhận hồ sơ, chuyển sang xử lý
                        nextStatus = "PROCESSING";
                        dossier.setDossierStatus(nextStatus);

                        // Gán cho cán bộ xử lý tiếp theo (Land Officer)
                        if (dto.getNextHandlerId() != null) {
                                SysUser handler = new SysUser();
                                handler.setId(dto.getNextHandlerId());
                                dossier.setCurrentHandler(handler);
                        }

                        opsDossierRepository.save(dossier);

                        // Tạo log và liên kết với bước workflow (Step 1)
                        OpsDossierLog log = createLog(dossier, dto.getHandlerId(),
                                        "VERIFY_OK", prevStatus, nextStatus,
                                        dto.getComments());
                        linkWorkflowStep(log, 1); // Bước 1: Tiếp nhận

                } else if ("REJECT".equals(action)) {
                        nextStatus = "REJECTED";
                        dossier.setDossierStatus(nextStatus);
                        dossier.setRejectionReason(dto.getComments());
                        opsDossierRepository.save(dossier);

                        createLog(dossier, dto.getHandlerId(), "REJECT",
                                        prevStatus, nextStatus, dto.getComments());

                } else if ("REQUIRE_SUPPLEMENT".equals(action)) {
                        nextStatus = "REQUIRE_SUPPLEMENT";
                        dossier.setDossierStatus(nextStatus);
                        opsDossierRepository.save(dossier);

                        createLog(dossier, dto.getHandlerId(), "REQUEST_SUPPLEMENT",
                                        prevStatus, nextStatus, dto.getComments());
                }

                return dossier;
        }

        // ==================== BƯỚC 3: THẨM ĐỊNH NHU CẦU ====================
        @Transactional
        public OpsDossier appraiseDossier(DossierAppraisalDTO dto) {
                OpsDossier dossier = opsDossierRepository.findById(dto.getDossierId())
                                .orElseThrow(() -> new RuntimeException("Dossier not found"));

                String prevStatus = dossier.getDossierStatus();

                if ("PASS".equals(dto.getResult())) {
                        // Thẩm định đạt, giữ nguyên PROCESSING, chờ lấy ý kiến quy hoạch
                        OpsDossierLog log = createLog(dossier, dto.getOfficerId(),
                                        "APPRAISE_PASS", prevStatus, "PROCESSING",
                                        dto.getComments());
                        linkWorkflowStep(log, 2); // Bước 2: Thẩm định

                } else {
                        // Thẩm định không đạt, trả lại
                        dossier.setDossierStatus("REJECTED");
                        dossier.setRejectionReason(dto.getComments());
                        opsDossierRepository.save(dossier);

                        createLog(dossier, dto.getOfficerId(), "APPRAISE_FAIL",
                                        prevStatus, "REJECTED", dto.getComments());
                }

                return dossier;
        }

        // ==================== BƯỚC 4: LẤY Ý KIẾN QUY HOẠCH ====================
        @Transactional
        public OpsDossier submitToLeader(Long dossierId, Long officerId,
                        Long chairmanId, String comments) {
                OpsDossier dossier = opsDossierRepository.findById(dossierId)
                                .orElseThrow(() -> new RuntimeException("Dossier not found"));

                String prevStatus = dossier.getDossierStatus();

                // Gán cho Chủ tịch
                SysUser chairman = new SysUser();
                chairman.setId(chairmanId);
                dossier.setCurrentHandler(chairman);
                opsDossierRepository.save(dossier);

                // Tạo log
                OpsDossierLog log = createLog(dossier, officerId,
                                "SUBMIT_TO_LEADER", prevStatus, "PROCESSING",
                                comments);
                linkWorkflowStep(log, 3); // Bước 3: Lấy ý kiến quy hoạch

                return dossier;
        }

        // ==================== BƯỚC 5: CHỦ TỊCH PHÊ DUYỆT ====================
        @Transactional
        public OpsDossier chairmanApprove(DossierApprovalDTO dto) {
                OpsDossier dossier = opsDossierRepository.findById(dto.getDossierId())
                                .orElseThrow(() -> new RuntimeException("Dossier not found"));

                String prevStatus = dossier.getDossierStatus();

                if ("APPROVE".equals(dto.getDecision())) {
                        // Duyệt, chuyển về Địa chính để cập nhật
                        OpsDossierLog log = createLog(dossier, dto.getChairmanId(),
                                        "APPROVE", prevStatus, "PROCESSING",
                                        dto.getComments());
                        linkWorkflowStep(log, 4); // Bước 4: Phê duyệt

                } else {
                        // Từ chối
                        dossier.setDossierStatus("REJECTED");
                        dossier.setRejectionReason(dto.getComments());
                        opsDossierRepository.save(dossier);

                        createLog(dossier, dto.getChairmanId(), "CHAIRMAN_REJECT",
                                        prevStatus, "REJECTED", dto.getComments());
                }

                return dossier;
        }

        // ==================== BƯỚC 6A: CẬP NHẬT SỔ ĐẤT ====================
        @Transactional
        public OpsDossier updateLandRecord(Long dossierId, Long officerId,
                        String comments) {
                OpsDossier dossier = opsDossierRepository.findById(dossierId)
                                .orElseThrow(() -> new RuntimeException("Dossier not found"));

                String prevStatus = dossier.getDossierStatus();

                // Cập nhật dữ liệu đất đai (logic cụ thể tùy nghiệp vụ)
                // ... update mock_lands table ...

                // Tạo log
                OpsDossierLog log = createLog(dossier, officerId,
                                "UPDATE_LAND_DB", prevStatus, "PROCESSING",
                                comments);
                linkWorkflowStep(log, 5); // Bước 5: Cập nhật sổ

                return dossier;
        }

        // ==================== BƯỚC 6B: TRẢ KẾT QUẢ ====================
        @Transactional
        public OpsDossier finishDossier(Long dossierId, Long handlerId,
                        String comments) {
                OpsDossier dossier = opsDossierRepository.findById(dossierId)
                                .orElseThrow(() -> new RuntimeException("Dossier not found"));

                String prevStatus = dossier.getDossierStatus();

                // Hoàn tất hồ sơ
                dossier.setDossierStatus("APPROVED");
                dossier.setFinishDate(LocalDateTime.now());
                opsDossierRepository.save(dossier);

                // Tạo log
                OpsDossierLog log = createLog(dossier, handlerId,
                                "FINISH", prevStatus, "APPROVED",
                                comments);
                linkWorkflowStep(log, 6); // Bước 6: Trả kết quả

                return dossier;
        }

        // ==================== HELPER METHODS ====================
        private OpsDossierLog createLog(OpsDossier dossier, Long actorId,
                        String action, String prevStatus,
                        String nextStatus, String comments) {
                OpsDossierLog log = new OpsDossierLog();
                log.setDossier(dossier);
                log.setActorId(actorId); // OpsDossierLog sử dụng actorId (Long) không phải actor (SysUser)
                log.setAction(action);
                log.setPrevStatus(prevStatus);
                log.setNextStatus(nextStatus);
                log.setComments(comments);
                // createdAt được tự động set bởi BaseEntity, không cần set thủ công

                return logRepo.save(log);
        }

        private void linkWorkflowStep(OpsDossierLog log, int stepOrder) {
                // Tìm workflow step theo service và step_order
                CatWorkflowStep step = catWorkflowRepo.findByServiceIdAndStepOrder(
                                log.getDossier().getService().getId(),
                                stepOrder);

                if (step != null) {
                        OpsLogWorkflowStep link = new OpsLogWorkflowStep();
                        link.setLog(log);
                        link.setWorkflowStep(step);
                        link.setDescription("Hoàn thành bước: " + step.getStepName());
                        workflowStepRepo.save(link);
                }
        }

}
