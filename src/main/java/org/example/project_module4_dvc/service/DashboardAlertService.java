package org.example.project_module4_dvc.service;

import lombok.RequiredArgsConstructor;
import org.example.project_module4_dvc.dto.admin.AlertDTO;
import org.example.project_module4_dvc.repository.mod.ModFeedbackRepository;
import org.example.project_module4_dvc.repository.ops.OpsDossierRepository;
import org.example.project_module4_dvc.service.iml.IDashboardAlertService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardAlertService implements IDashboardAlertService {

    private final OpsDossierRepository opsDossierRepository;
    private final ModFeedbackRepository modFeedbackRepository;

    @Override
    public Page<AlertDTO> getAlerts(Pageable pageable) {

        List<AlertDTO> alerts = new ArrayList<>();

        // 1. Hồ sơ quá hạn
        opsDossierRepository.findOverdueDossiers().forEach(d -> {
            alerts.add(new AlertDTO(
                    d.getId(),
                    "OVERDUE",
                    d.getDossierCode(),
                    "Hồ sơ quá hạn xử lý",
                    d.getService().getDomain(),
                    (int) ChronoUnit.DAYS.between(
                            d.getDueDate().toLocalDate(),
                            LocalDate.now())
            ));
        });

//         2. Hồ sơ sắp hết hạn (<=3 ngày)
        opsDossierRepository.findNearlyDue(
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(3)
        ).forEach(d -> {
            alerts.add(new AlertDTO(
                    d.getId(),
                    "NEARLY_DUE",
                    d.getDossierCode(),
                    "Hồ sơ sắp đến hạn",
                    d.getService().getDomain(),
                    (int) ChronoUnit.DAYS.between(
                            LocalDate.now(),
                            d.getDueDate().toLocalDate())
            ));
        });

        // 3. Feedback chưa xử lý
        modFeedbackRepository.findByIsResolvedFalseOrderByCreatedAtDesc()
                .forEach(fb -> {
                    alerts.add(new AlertDTO(
                            fb.getId(),
                            "FEEDBACK",
                            fb.getTitle(),
                            fb.getContent(),
                            null,
                            null
                    ));
                });

        // Ưu tiên: OVERDUE > NEARLY_DUE > FEEDBACK
        alerts.sort(Comparator.comparing(AlertDTO::getType));

        // ===== PHÂN TRANG THỦ CÔNG =====
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), alerts.size());

        List<AlertDTO> pageContent =
                start > alerts.size() ? List.of() : alerts.subList(start, end);

        return new PageImpl<>(pageContent, pageable, alerts.size());
    }
}