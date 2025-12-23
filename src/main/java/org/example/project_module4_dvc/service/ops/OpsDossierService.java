package org.example.project_module4_dvc.service.ops;

import org.example.project_module4_dvc.dto.OpsDossierDTO.CitizenNotificationProjection;
import org.example.project_module4_dvc.dto.OpsDossierDTO.OpsDossierDetailDTO;
import org.example.project_module4_dvc.dto.OpsDossierDTO.OpsDossierSummaryDTO;
import org.example.project_module4_dvc.dto.admin.ChartDataDTO;
import org.example.project_module4_dvc.dto.admin.ChartDatasetDTO;
import org.example.project_module4_dvc.entity.ops.OpsDossier;
import org.example.project_module4_dvc.repository.ops.OpsDossierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.example.project_module4_dvc.dto.formData.BirthRegistrationFormDTO;
import org.example.project_module4_dvc.service.websocket.IWebsocketService;

@Service
public class OpsDossierService implements IOpsDossierService {
    @Autowired
    private OpsDossierRepository opsDossierRepository;
    @Autowired
    private org.example.project_module4_dvc.repository.sys.SysUserRepository sysUserRepository;
    @Autowired
    private org.example.project_module4_dvc.repository.cat.CatServiceRepository catServiceRepository;
    @Autowired
    private org.example.project_module4_dvc.repository.ops.OpsDossierLogRepository dossierLogRepository;
    @Autowired
    private org.example.project_module4_dvc.repository.sys.SysDepartmentRepository sysDepartmentRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private IWebsocketService websocketService;

    @Override
    public Page<OpsDossierSummaryDTO> getMyDossierList(Long userId, Pageable pageable) {
        return opsDossierRepository.findDossiersByApplicantId(userId, pageable);
    }

    @Override
    public Page<OpsDossierSummaryDTO> searchMyDossiers(Long userId, String keyword, String status, Pageable pageable) {
        String searchKeyword = (keyword != null && !keyword.trim().isEmpty()) ? "%" + keyword.trim() + "%" : null;
        String searchStatus = (status != null && !status.trim().isEmpty()) ? status.trim() : null;
        return opsDossierRepository.searchDossiersByApplicant(userId, searchKeyword, searchStatus, pageable);
    }

    @Override
    public OpsDossierDetailDTO getDossierDetail(Long id) {
        return opsDossierRepository.findDossierDetailById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ với ID: " + id));
    }

    @Override
    public Map<String, Long> getStatusCountByUser(Long userId) {
        List<Object[]> rows = opsDossierRepository.countStatusesByApplicant(userId);

        Map<String, Long> result = new HashMap<>();
        for (Object[] row : rows) {
            String status = row[0].toString();
            Long count = (Long) row[1];
            result.put(status, count);
        }
        return result;
    }

    @Override
    public List<CitizenNotificationProjection> getTop3MyNotifications(Long userId) {
        return opsDossierRepository.findTop3NotificationsByApplicant(userId);
    }

    @Override
    public Page<CitizenNotificationProjection> getAllMyNotifications(Long userId, Pageable pageable) {
        return opsDossierRepository.findAllNotificationsByApplicant(userId, pageable);
    }

    // ==========================================
    // merged from service.OpsDossierService
    // ==========================================

    @Override
    public List<Map<String, Object>> getDossierAlerts() {
        List<org.example.project_module4_dvc.entity.ops.OpsDossier> pendingDossiers = opsDossierRepository
                .findByDossierStatusNotIn(
                        java.util.Arrays.asList("APPROVED", "REJECTED"));

        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        List<Map<String, Object>> dossierAlerts = new java.util.ArrayList<>();

        for (org.example.project_module4_dvc.entity.ops.OpsDossier d : pendingDossiers) {
            if (d.getDueDate() != null) {
                long daysDiff = java.time.temporal.ChronoUnit.DAYS.between(now.toLocalDate(),
                        d.getDueDate().toLocalDate());

                Map<String, Object> alert = new HashMap<>();
                alert.put("id", d.getId());
                alert.put("code", d.getDossierCode());
                alert.put("domain", d.getService().getDomain());

                if (daysDiff < 0) { // Quá hạn
                    alert.put("type", "OVERDUE");
                    alert.put("days", Math.abs(daysDiff));
                } else if (daysDiff <= 1) { // Sắp đến hạn trong 3 ngày
                    alert.put("type", "NEARLY_DUE");
                    alert.put("days", daysDiff);
                } else {
                    continue; // Không cần cảnh báo
                }

                dossierAlerts.add(alert);
            }
        }
        return dossierAlerts;
    }

    // ===== SUMMARY =====
    @Override
    public Map<String, Long> getSummary() {

        Map<String, Long> map = new HashMap<>();
        map.put("total", opsDossierRepository.countThisMonth());
        map.put("processing", opsDossierRepository.countByDossierStatus("PENDING"));
        map.put("completed", opsDossierRepository.countByDossierStatus("APPROVED"));
        map.put("overdue", opsDossierRepository.countOverdue());

        return map;
    }

    // ===== CHART =====
    @Override
    public ChartDataDTO getChartData() {

        List<String> domains = opsDossierRepository.findAllDomains();
        List<String> statuses = List.of("NEW", "PENDING", "APPROVED", "REJECTED");

        List<Object[]> raw = opsDossierRepository.countByDomainAndStatus();

        // domain -> status -> count
        Map<String, Map<String, Long>> map = new HashMap<>();

        for (Object[] r : raw) {
            String domain = (String) r[0];
            String status = (String) r[1];
            Long count = (Long) r[2];

            map.computeIfAbsent(domain, k -> new HashMap<>())
                    .put(status, count);
        }

        List<ChartDatasetDTO> datasets = new ArrayList<>();

        for (String status : statuses) {
            List<Long> data = new ArrayList<>();
            for (String domain : domains) {
                data.add(
                        map.getOrDefault(domain, Map.of())
                                .getOrDefault(status, 0L));
            }
            datasets.add(new ChartDatasetDTO(status, data));
        }

        return new ChartDataDTO(domains, datasets);
    }

    @Override
    public List<OpsDossier> getOverdueDossiers() {
        return opsDossierRepository.findOverdueDossiers();
    }

    @Override
    public Page<Map<String, Object>> getOverdueAlerts(Pageable pageable) {
        return opsDossierRepository.findOverdueAlerts(pageable);
    }

    @Override
    public Page<Map<String, Object>> getNearlyDueAlerts(Pageable pageable) {
        return opsDossierRepository.findNearlyDueAlerts(pageable);
    }

    @Override
    public int getOnTimeRate() {
        long completed = opsDossierRepository.countCompleted();

        if (completed == 0) {
            return 100; // hoặc 0 tùy nghiệp vụ
        }

        long onTime = opsDossierRepository.countCompletedOnTime();

        return Math.round((onTime * 100f) / completed);
    }

    @Override
    public int calculateOnTimeRateStrict() {

        long total = opsDossierRepository.countThisMonth();
        if (total == 0) {
            return 100;
        }

        long onTime = opsDossierRepository.countOverdue();

        return Math.round(((total - onTime) * 100f) / total);
    }

    @Override
    public Page<OpsDossier> getAdminDossierPage(Pageable pageable) {
        return opsDossierRepository.findAll(pageable);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public void submitBirthRegistration(org.example.project_module4_dvc.dto.BirthRegistrationRequest request,
            Long userId) {
        // 1. Fetch User
        var applicant = sysUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Fetch Service
        // Start with a hardcoded Service ID or Code if not passed, or use
        // request.serviceId
        // For safety, let's assume service ID 1 is Birth Registration or look it up by
        // code
        var service = catServiceRepository.findById(request.getServiceId() != null ? request.getServiceId() : 1L)
                .orElseThrow(() -> new RuntimeException("Service not found"));

        // 3. Validation Logic
        String gender = request.getApplicantGender();
        String status = request.getApplicantMaritalStatus();

        boolean isMarried = "married".equalsIgnoreCase(status);

        if ("male".equalsIgnoreCase(gender) && !isMarried) {
            // Case 1: Father + Not Married
            if (request.getMotherName() == null || request.getMotherName().trim().isEmpty()) {
                throw new IllegalArgumentException(
                        "Trường hợp Cha chưa kết hôn đăng ký khai sinh bắt buộc phải khai thông tin Mẹ!");
            }
            // Logic relaxed: Officer will verify paternity recognition during processing
        } else if ("female".equalsIgnoreCase(gender) && !isMarried) {
            // Case 2: Mother + Not Married
            // Logic relaxed: Officer will verify paternity recognition during processing
        }

        // 4. Generate Dossier Code
        // Format: HS-{PREFIX}-{SEQUENCE}
        // Example: HS-HK01-0001
        String servicePrefix = service.getServiceCode().split("_")[0]; // HK01_TRE -> HK01
        String prefix = "HS-" + servicePrefix + "-";

        String nextCode;
        java.util.Optional<String> latestCodeOpt = opsDossierRepository.findLatestDossierCode(prefix);
        if (latestCodeOpt.isPresent()) {
            String latestCode = latestCodeOpt.get();
            try {
                // Extract sequence number (last 4 digits)
                String seqStr = latestCode.substring(latestCode.lastIndexOf("-") + 1);
                int seq = Integer.parseInt(seqStr);
                nextCode = prefix + String.format("%04d", seq + 1);
            } catch (Exception e) {
                // Fallback if parsing fails
                nextCode = prefix + "0001";
            }
        } else {
            nextCode = prefix + "0001";
        }

        // 5. Calculate Due Date
        java.time.LocalDateTime submissionDate = java.time.LocalDateTime.now();
        java.time.LocalDateTime dueDate = null;
        if (service.getSlaHours() != null && service.getSlaHours() > 0) {
            // Simple calculation: add SLA hours.
            // Note: Real world might involve business hours/holidays calculation.
            dueDate = submissionDate.plusHours(service.getSlaHours());
        }

        // 6. Save Dossier
        String childGenderRaw = request.getChildGender();
        String childGender = (childGenderRaw != null) ? childGenderRaw.trim() : "";
        if ("Nam".equalsIgnoreCase(childGender))
            childGender = "MALE";
        else if ("Nữ".equalsIgnoreCase(childGender))
            childGender = "FEMALE";
        else
            childGender = childGender.toUpperCase();

        BirthRegistrationFormDTO formDto = new BirthRegistrationFormDTO();
        formDto.setChildFullName(request.getChildName());
        formDto.setDateOfBirth(request.getChildDob());
        formDto.setGender(childGender);
        formDto.setPlaceOfBirth(request.getChildBirthPlace());
        formDto.setFatherFullName(request.getFatherName());
        formDto.setFatherIdNumber(request.getFatherId());
        formDto.setMotherFullName(request.getMotherName());
        formDto.setMotherIdNumber(request.getMotherId());
        formDto.setRegisteredAddress(request.getRegisteredAddress());
        formDto.setRequestBhyt(request.isRequestBhyt());

        // Convert DTO to Map for the Persistent Layer (JsonToMapConverter)
        Map<String, Object> formData = objectMapper.convertValue(formDto, new TypeReference<Map<String, Object>>() {
        });

        // Add additional non-DTO fields if needed by other logic
        formData.put("DEBUG_VERSION", "2.0");
        formData.put("applicantGender", gender != null ? gender.toUpperCase() : null);
        formData.put("applicantMaritalStatus", status != null ? status.toUpperCase() : null);
        formData.put("isPaternityRecognition", request.isPaternityRecognition());
        formData.put("childEthnicity", request.getChildEthnicity());

        // Fallback for receivingDept if Service doesn't have one (DB missing data)
        var receivingDept = service.getDepartment();
        if (receivingDept == null) {
            receivingDept = sysDepartmentRepository.findAll().stream()
                    .filter(d -> d.getLevel() == 2)
                    .findFirst()
                    .orElse(null);

            if (receivingDept == null) {
                receivingDept = sysDepartmentRepository.findById(2L).orElse(null);
            }
        }

        // Check Overdue Note
        String internalNote = null;
        if (request.getChildDob() != null) {
            long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(request.getChildDob(),
                    java.time.LocalDate.now());
            if (daysBetween > 60) {
                internalNote = "CẢNH BÁO: Đăng ký khai sinh quá hạn (quá " + daysBetween + " ngày).";
            }
        }

        OpsDossier dossier = OpsDossier.builder()
                .dossierCode(nextCode)
                .dossierStatus("NEW")
                .submissionDate(submissionDate)
                .dueDate(dueDate)
                .receivingDept(receivingDept)
                .service(service)
                .applicant(applicant)
                .formData(formData)
                .build();

        opsDossierRepository.save(dossier);

        // 7. Log Interaction
        // (Optional: You might want to add an entry to ops_dossier_logs here)
        org.example.project_module4_dvc.entity.ops.OpsDossierLog log = new org.example.project_module4_dvc.entity.ops.OpsDossierLog();
        log.setDossier(dossier);
        log.setActorId(applicant.getId());
        log.setAction("NOP_HO_SO");
        log.setComments("Công dân nộp hồ sơ trực tuyến");
        log.setNextStatus("NEW");
        log.setCreatedAt(submissionDate);

        dossierLogRepository.save(log);

        // 8. WebSocket Notify
        if (receivingDept != null) {
            websocketService.broadcastNewDossierToList(receivingDept.getDeptName(), dossier);
        }
    }
}