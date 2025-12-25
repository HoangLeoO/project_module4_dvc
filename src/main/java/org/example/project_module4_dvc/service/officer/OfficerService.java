package org.example.project_module4_dvc.service.officer;

import org.example.project_module4_dvc.dto.dossier.NewDossierDTO;
import org.example.project_module4_dvc.dto.dossier.ResultDossierDTO;
import org.example.project_module4_dvc.dto.specialist.SpecialistAvailableDTO;
import org.example.project_module4_dvc.dto.websocket.DossierUpdateMessage;
import org.example.project_module4_dvc.entity.ops.OpsDossier;
import org.example.project_module4_dvc.entity.ops.OpsDossierFile;
import org.example.project_module4_dvc.entity.sys.SysUser;
import org.example.project_module4_dvc.mapper.OpsDossierMapper;
import org.example.project_module4_dvc.mapper.OpsDossierResultMapper;
import org.example.project_module4_dvc.repository.ops.OpsDossierFileRepository;
import org.example.project_module4_dvc.repository.ops.OpsDossierRepository;
import org.example.project_module4_dvc.repository.cat.CatWorkflowStepRepository;
import org.example.project_module4_dvc.repository.ops.OpsDossierLogRepository;
import org.example.project_module4_dvc.repository.ops.OpsLogWorkflowStepRepository;
import org.example.project_module4_dvc.repository.ops.OpsDossierResultRepository;
import org.example.project_module4_dvc.repository.sys.SysUserRepository;
import org.example.project_module4_dvc.service.websocket.IWebsocketService;
import org.example.project_module4_dvc.entity.ops.OpsDossierLog;
import org.example.project_module4_dvc.entity.ops.OpsLogWorkflowStep;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OfficerService implements IOfficerService {
        private final OpsDossierMapper opsDossierMapper;
        private final OpsDossierResultMapper opsDossierResultMapper;
        private final OpsDossierRepository opsDossierRepository;
        private final OpsDossierFileRepository opsDossierFileRepository;

        private final OpsDossierResultRepository opsDossierResultRepository;
        private final SysUserRepository sysUserRepository;
        private final OpsDossierLogRepository opsDossierLogRepository;
        private final OpsLogWorkflowStepRepository opsLogWorkflowStepRepository;
        private final CatWorkflowStepRepository catWorkflowStepRepository;

        private final org.example.project_module4_dvc.repository.mock.MockBusinessRepository mockBusinessRepository;
        private final org.example.project_module4_dvc.repository.mock.MockCitizenRepository mockCitizenRepository;

        private final IWebsocketService websocketService;

        public OfficerService(OpsDossierMapper opsDossierMapper, OpsDossierResultMapper opsDossierResultMapper,
                        OpsDossierRepository opsDossierRepository, OpsDossierFileRepository opsDossierFileRepository,
                        OpsDossierResultRepository opsDossierResultRepository, SysUserRepository sysUserRepository,
                        OpsDossierLogRepository opsDossierLogRepository,
                        OpsLogWorkflowStepRepository opsLogWorkflowStepRepository,
                        CatWorkflowStepRepository catWorkflowStepRepository,
                        org.example.project_module4_dvc.repository.mock.MockBusinessRepository mockBusinessRepository,
                        org.example.project_module4_dvc.repository.mock.MockCitizenRepository mockCitizenRepository,
                        IWebsocketService websocketService) {
                this.opsDossierMapper = opsDossierMapper;
                this.opsDossierResultMapper = opsDossierResultMapper;
                this.opsDossierRepository = opsDossierRepository;
                this.opsDossierFileRepository = opsDossierFileRepository;
                this.opsDossierResultRepository = opsDossierResultRepository;
                this.sysUserRepository = sysUserRepository;
                this.opsDossierLogRepository = opsDossierLogRepository;
                this.opsLogWorkflowStepRepository = opsLogWorkflowStepRepository;
                this.catWorkflowStepRepository = catWorkflowStepRepository;
                this.mockBusinessRepository = mockBusinessRepository;
                this.mockCitizenRepository = mockCitizenRepository;
                this.websocketService = websocketService;
        }

        @Override
        public List<SpecialistAvailableDTO> getAvailableSpecialists(Long dossierId) {
                return sysUserRepository.findAvailableSpecialistsForDossier(dossierId);
        }

        // ho so vua tao
        @Override
        public Page<NewDossierDTO> findAll(String dossierStatus, String departmentName, String paymentStatus,Pageable pageable) {
                return opsDossierRepository
                                .findOpsDossierByDossierStatusAndReceivingDept_DeptNameAndPaymentStatus(dossierStatus, departmentName,paymentStatus,
                                                pageable)
                                .map(opsDossierMapper::toDTO);
        }

        // kq tra ve
        @Override
        public Page<ResultDossierDTO> findAllResult(String departmentName, Pageable pageable) {
                return opsDossierResultRepository.findOpsDossierResultByDossier_ReceivingDept_DeptName(
                                departmentName, pageable).map(opsDossierResultMapper::toDTO);
        }

        @Override
        public NewDossierDTO findById(Long id) {
                return opsDossierMapper.toDTO(opsDossierRepository.findById(id).orElse(null));
        }

        @Override
        public List<OpsDossierFile> findFileByDossierId(Long dossierId) {
                return opsDossierFileRepository.findOpsDossierFileByDossier_Id(dossierId).stream().toList();
        }

        @Override
        public List<NewDossierDTO> findNearlyDue(String departmentName) {
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime limit = now.plusHours(6);
                return opsDossierRepository.findNearlyDue(now, limit, departmentName).stream()
                                .map(opsDossierMapper::toDTO)
                                .toList();
        }

        @Override
        @Transactional
        public void updateDossierStatus(Long dossierId, String status, Long specialistId, LocalDateTime dueDate,
                        String reason) {
                OpsDossier opsDossier = opsDossierRepository.findById(dossierId).orElse(null);
                if (opsDossier != null) {
                        String oldStatus = opsDossier.getDossierStatus();
                        opsDossier.setDossierStatus(status);
                        opsDossier.setDueDate(dueDate);
                        opsDossier.setCurrentHandler(sysUserRepository.findById(specialistId).orElse(null));
                        opsDossier.setRejectionReason(reason);
                        OpsDossier saved = opsDossierRepository.save(opsDossier);

                        if ("PENDING".equals(status)) {
                                /*
                                 * LOGGING HANDLED BY TRIGGER
                                 * recordStepCompletion(saved, specialistId, "ACCEPTED", oldStatus, "PENDING",
                                 * "Hồ sơ đã được tiếp nhận",
                                 * 1);
                                 */
                        } else if ("APPROVED".equals(status)) {
                                /*
                                 * LOGGING HANDLED BY TRIGGER
                                 * recordStepCompletion(saved, specialistId, "PHE_DUYET", oldStatus, "APPROVED",
                                 * "Hồ sơ đã được phê duyệt", 3);
                                 */
                        } else if ("RESULT_RETURNED".equals(status)) {
                                /*
                                 * LOGGING HANDLED BY TRIGGER
                                 * recordStepCompletion(saved, specialistId, "TRA_KET_QUA", oldStatus,
                                 * "RESULT_RETURNED",
                                 * "Hồ sơ đã được trả kết quả cho công dân", 4);
                                 */

                                // Check duplicate/update if needed, but redundant if APPROVED already did it.
                                // =================================================================
                                // 🔥 LOGIC MỚI: TỰ ĐỘNG TẠO DOANH NGHIỆP KHI TRẢ KẾT QUẢ (KD01_HKD)
                                // =================================================================
                                if (saved.getService().getServiceCode().equals("KD01_HKD")) {
                                        try {
                                                createBusinessFromDossier(saved);
                                        } catch (Exception e) {
                                                System.err.println("Lỗi tạo doanh nghiệp: " + e.getMessage());
                                                e.printStackTrace();
                                        }
                                }
                        }

                        // Real-time updates
                        websocketService.broadcastDossierUpdate(saved.getReceivingDept().getDeptName(), saved);
                        websocketService.notifyStatusChange(saved.getApplicant().getUsername(),
                                        DossierUpdateMessage.builder()
                                                        .dossierId(saved.getId())
                                                        .dossierCode(saved.getDossierCode())
                                                        .oldStatus(oldStatus)
                                                        .newStatus(status)
                                                        .handlerName(saved.getCurrentHandler() != null
                                                                        ? saved.getCurrentHandler().getFullName()
                                                                        : null)
                                                        .build());
                }

        }

        @Override
        @Transactional
        public void updateDossierRejectStatus(Long dossierId, String status, String reason) {
                OpsDossier opsDossier = opsDossierRepository.findById(dossierId).orElse(null);
                if (opsDossier != null) {
                        String oldStatus = opsDossier.getDossierStatus();
                        opsDossier.setDossierStatus(status);
                        opsDossier.setRejectionReason(reason);
                        OpsDossier saved = opsDossierRepository.save(opsDossier);

                        // Real-time updates
                        websocketService.broadcastDossierRemoval(saved.getReceivingDept().getDeptName(), saved.getId(),
                                        saved.getDossierCode());
                        websocketService.notifyStatusChange(saved.getApplicant().getUsername(),
                                        DossierUpdateMessage.builder()
                                                        .dossierId(saved.getId())
                                                        .dossierCode(saved.getDossierCode())
                                                        .oldStatus(oldStatus)
                                                        .newStatus(status)
                                                        .comment(reason)
                                                        .build());
                }
        }

        // websocket
        @Override
        @Transactional
        public void assignDossierToSpecialist(Long dossierId, Long specialistId) {
                OpsDossier dossier = opsDossierRepository.findById(dossierId)
                                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ"));

                SysUser specialist = sysUserRepository.findById(specialistId)
                                .orElseThrow(() -> new RuntimeException("Không tìm thấy chuyên viên"));

                String oldStatus = dossier.getDossierStatus();
                String departmentName = dossier.getReceivingDept().getDeptName();

                // Update entity
                dossier.setCurrentHandler(specialist);
                dossier.setDossierStatus("PENDING");
                OpsDossier saved = opsDossierRepository.save(dossier);

                // 🔥 Real-time updates
                // 1. Update officer's list
                websocketService.broadcastDossierUpdate(departmentName, saved);

                // 2. Add to specialist's list
                websocketService.sendToSpecialistList(
                                specialist.getUsername(),
                                saved,
                                "ADD");

                // 3. Send notifications
                websocketService.notifyAssignment(
                                specialist.getUsername(),
                                saved.getId(),
                                saved.getDossierCode(),
                                saved.getService().getServiceName());

                websocketService.notifyStatusChange(
                                saved.getApplicant().getUsername(),
                                DossierUpdateMessage.builder()
                                                .dossierId(saved.getId())
                                                .dossierCode(saved.getDossierCode())
                                                .oldStatus(oldStatus)
                                                .newStatus("PENDING")
                                                .handlerName(specialist.getFullName())
                                                .action("ASSIGNED")
                                                .build());

                // 4. Record Workflow Step Completion (Step 1: Tiếp nhận hồ sơ)
                recordStepCompletion(saved, specialistId, "ASSIGNED", oldStatus, "PENDING",
                                "Đã tiếp nhận và phân công xử lý",
                                1);
        }

        private void recordStepCompletion(OpsDossier dossier, Long actorId, String action, String prevStatus,
                        String nextStatus, String comments, int stepOrder) {
                // Create Log
                OpsDossierLog log = OpsDossierLog.builder()
                                .dossier(dossier)
                                .actorId(actorId)
                                .action(action)
                                .prevStatus(prevStatus)
                                .nextStatus(nextStatus)
                                .comments(comments)
                                .build();
                OpsDossierLog savedLog = opsDossierLogRepository.save(log);

                // Find Step for this service
                catWorkflowStepRepository.findAll().stream()
                                .filter(s -> s.getService().getId().equals(dossier.getService().getId())
                                                && s.getStepOrder() == stepOrder)
                                .findFirst()
                                .ifPresent(step -> {
                                        OpsLogWorkflowStep lws = new OpsLogWorkflowStep();
                                        lws.setLog(savedLog);
                                        lws.setWorkflowStep(step);
                                        lws.setDescription(step.getStepName() + " hoàn thành");
                                        lws.setCreatedAt(java.time.Instant.now());
                                        opsLogWorkflowStepRepository.save(lws);
                                });
        }

        private void createBusinessFromDossier(OpsDossier dossier) {
                java.util.Map<String, Object> formData = dossier.getFormData();
                if (formData == null)
                        return;

                // 1. Generate Tax Code (MST)
                String maxTaxCode = mockBusinessRepository.findMaxTaxCode();
                String newTaxCode;
                if (maxTaxCode != null && maxTaxCode.matches("\\d+")) {
                        try {
                                long currentMax = Long.parseLong(maxTaxCode);
                                newTaxCode = String.format("%010d", currentMax + 1);
                        } catch (NumberFormatException e) {
                                newTaxCode = "0400000001"; // Fallback
                        }
                } else {
                        newTaxCode = "0400000001"; // Default start
                }

                // 2. Extract Data
                String businessName = (String) formData.get("businessName");
                String capitalStr = String.valueOf(formData.get("registeredCapital"));
                java.math.BigDecimal capital = new java.math.BigDecimal(capitalStr.replaceAll("[^0-9.]", ""));
                String ownerIdNumber = (String) formData.get("ownerIdNumber");
                String address = (String) formData.get("businessAddress");
                String businessLines = (String) formData.get("businessLine");

                // 3. Find Owner
                org.example.project_module4_dvc.entity.mock.MockCitizen owner = mockCitizenRepository
                                .findByCccd(ownerIdNumber)
                                .orElse(null);

                if (owner == null) {
                        // Try finding by applicant if ownerIdNumber lookup fails
                        // Assumes SysUser has getCitizen() method returning MockCitizen
                        // If SysUser has getCitizenId(), use it.
                        // Let's assume SysUser has a reference to MockCitizen named 'citizen' based on
                        // earlier context or generic pattern.
                        // If it is an ID, it would be getCitizenId() but lint said it's undefined.
                        // Checking DDL, sys_users has citizen_id. In JPA it's likely 'private
                        // MockCitizen citizen;'.
                        if (dossier.getApplicant().getCitizen() != null) {
                                owner = dossier.getApplicant().getCitizen();
                        }
                }

                if (owner == null) {
                        System.err.println(
                                        "Không tìm thấy thông tin chủ hộ (MockCitizen) để gán cho doanh nghiệp mới.");
                        return;
                }

                // 4. Save Business
                org.example.project_module4_dvc.entity.mock.MockBusiness business = new org.example.project_module4_dvc.entity.mock.MockBusiness();
                business.setTaxCode(newTaxCode);
                business.setBusinessName(businessName);
                business.setCapital(capital);
                business.setOwner(owner);
                business.setAddress(address);
                business.setBusinessLines(businessLines);

                mockBusinessRepository.save(business);
                System.out.println("Đã tạo mới doanh nghiệp/hộ kinh doanh: " + newTaxCode + " - " + businessName);
        }

}