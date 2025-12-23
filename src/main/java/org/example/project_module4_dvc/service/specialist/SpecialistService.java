package org.example.project_module4_dvc.service.specialist;

import org.example.project_module4_dvc.dto.dossier.NewDossierDTO;
import org.example.project_module4_dvc.entity.ops.OpsDossier;
import org.example.project_module4_dvc.mapper.OpsDossierMapper;
import org.example.project_module4_dvc.repository.cat.CatWorkflowStepRepository;
import org.example.project_module4_dvc.repository.mock.MockCitizenRepository;
import org.example.project_module4_dvc.repository.mock.MockHouseholdMemberRepository;
import org.example.project_module4_dvc.repository.mock.MockHouseholdRepository;
import org.example.project_module4_dvc.repository.ops.OpsDossierLogRepository;
import org.example.project_module4_dvc.repository.ops.OpsLogWorkflowStepRepository;
import org.example.project_module4_dvc.repository.ops.OpsDossierRepository;
import org.example.project_module4_dvc.repository.sys.SysUserRepository;
import org.example.project_module4_dvc.entity.mock.MockCitizen;
import org.example.project_module4_dvc.entity.mock.MockHouseholdMember;
import org.example.project_module4_dvc.entity.ops.OpsDossierLog;
import org.example.project_module4_dvc.entity.ops.OpsLogWorkflowStep;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SpecialistService implements ISpecialistService {
    private final OpsDossierRepository opsDossierRepository;

    private final SysUserRepository sysUserRepository;
    private final OpsDossierLogRepository opsDossierLogRepository;
    private final OpsLogWorkflowStepRepository opsLogWorkflowStepRepository;
    private final CatWorkflowStepRepository catWorkflowStepRepository;
    private final MockCitizenRepository mockCitizenRepository;
    private final MockHouseholdMemberRepository mockHouseholdMemberRepository;

    private final OpsDossierMapper opsDossierMapper;

    public SpecialistService(OpsDossierRepository opsDossierRepository, OpsDossierMapper opsDossierMapper,
            SysUserRepository sysUserRepository,
            OpsDossierLogRepository opsDossierLogRepository,
            OpsLogWorkflowStepRepository opsLogWorkflowStepRepository,
            CatWorkflowStepRepository catWorkflowStepRepository,
            MockCitizenRepository mockCitizenRepository,
            MockHouseholdMemberRepository mockHouseholdMemberRepository) {
        this.opsDossierRepository = opsDossierRepository;
        this.opsDossierMapper = opsDossierMapper;
        this.sysUserRepository = sysUserRepository;
        this.opsDossierLogRepository = opsDossierLogRepository;
        this.opsLogWorkflowStepRepository = opsLogWorkflowStepRepository;
        this.catWorkflowStepRepository = catWorkflowStepRepository;
        this.mockCitizenRepository = mockCitizenRepository;
        this.mockHouseholdMemberRepository = mockHouseholdMemberRepository;
    }

    @Override
    public Page<NewDossierDTO> findAll(String dossierStatus, String departmentName, Long specialistId,
            Pageable pageable) {
        return opsDossierRepository.findOpsDossierByDossierStatusAndReceivingDept_DeptNameAndCurrentHandler_Id(
                dossierStatus, departmentName, specialistId, pageable).map(opsDossierMapper::toDTO);
    }

    @Override
    public List<NewDossierDTO> findNearlyDue(String departmentName, Long specialistId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime limit = now.plusHours(6);
        return opsDossierRepository.findNearlyDueSpecialist(now, limit, departmentName, specialistId).stream()
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

            // Ghi log bước 2: Thẩm định hồ sơ
            if ("VERIFIED".equals(status)) {
                String comments = "Đã thẩm định hồ sơ, chờ lãnh đạo phê duyệt";
                // Nếu là hồ sơ khai sinh, thực hiện cập nhật dữ liệu dân cư
                if (saved.getService().getServiceCode().equals("HK01_TRE")) {
                    syncCitizenData(saved);
                    comments += " (Đã tạo mã số định danh, thẻ BHYT và cập nhật sổ hộ khẩu)";
                }
                recordStepCompletion(saved, specialistId, "THAM_DINH", oldStatus, "VERIFIED", comments, 2);
            }
        }
    }

    private void syncCitizenData(OpsDossier dossier) {
        java.util.Map<String, Object> data = dossier.getFormData();
        if (data == null)
            return;

        // 1. Tạo MockCitizen mới cho trẻ
        String childName = (String) data.get("childFullName");
        String dobStr = data.get("dateOfBirth") != null ? data.get("dateOfBirth").toString() : null;
        String gender = (String) data.get("gender");
        String fatherCccd = (String) data.get("fatherIdNumber");

        // Giả lập CCCD mới: 0 + Mã vùng + sequence
        // Đơn giản là tạo 1 số ngẫu nhiên 12 chữ số cho demo
        String newCccd = "0" + String.format("%011d", (long) (Math.random() * 100000000000L));

        MockCitizen child = MockCitizen.builder()
                .cccd(newCccd)
                .fullName(childName)
                .dob(dobStr != null ? java.time.LocalDate.parse(dobStr) : java.time.LocalDate.now())
                .gender(gender != null ? gender : "MALE")
                .maritalStatus("SINGLE")
                .status(1)
                .build();

        MockCitizen savedChild = mockCitizenRepository.save(child);

        // 2. Cập nhật vào sổ hộ khẩu của cha
        if (fatherCccd != null) {
            java.util.List<MockHouseholdMember> fatherMembers = mockHouseholdMemberRepository
                    .findByCitizen_CccdAndStatus(fatherCccd, 1);
            if (!fatherMembers.isEmpty()) {
                var household = fatherMembers.get(0).getHousehold();
                MockHouseholdMember newMember = MockHouseholdMember.builder()
                        .household(household)
                        .citizen(savedChild)
                        .relationToHead("Con")
                        .moveInDate(java.time.LocalDate.now())
                        .status(1)
                        .build();
                mockHouseholdMemberRepository.save(newMember);
            }
        }
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
}
