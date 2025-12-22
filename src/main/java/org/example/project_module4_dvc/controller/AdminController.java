package org.example.project_module4_dvc.controller;

import lombok.RequiredArgsConstructor;
import org.example.project_module4_dvc.service.iml.IDashboardAlertService;
import org.example.project_module4_dvc.service.mod.IModFeedbackService;
import org.example.project_module4_dvc.service.iml.ISysDepartmentService;
import org.springframework.data.domain.PageRequest;
import org.example.project_module4_dvc.service.ops.IOpsDossierService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;



@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final IOpsDossierService opsDossierService;
    private final IModFeedbackService modFeedbackService;
    private final ISysDepartmentService sysDepartmentService;
    private final IDashboardAlertService dashboardAlertService;

    @GetMapping("/")
    public String showDashboard(
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {
        model.addAttribute("summary", opsDossierService.getSummary());
        model.addAttribute("chartData", opsDossierService.getChartData());

        model.addAttribute("totalFeedbacks", modFeedbackService.totalFeedbacks());
        model.addAttribute("resolvedFeedbacks", modFeedbackService.resolvedFeedbacks());
        model.addAttribute("unresolvedFeedbacks", modFeedbackService.unresolvedFeedbacks());
        model.addAttribute("unresolvedFeedbackList", modFeedbackService.getUnresolvedFeedbacks());

        model.addAttribute("alerts",
                dashboardAlertService.getAlerts(PageRequest.of(page, 3)));
        model.addAttribute("onTimeRate",opsDossierService.calculateOnTimeRateStrict());
        return "pages/admin/admin-dashboard";
    }

    @GetMapping("/records")
    public String showRecords(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {
        // 1. Danh sách hồ sơ (phân trang)
        model.addAttribute(
                "dossiers",
                opsDossierService.getAdminDossierPage(
                        PageRequest.of(page, 10)
                )
        );

        // 2. Alert tổng
        model.addAttribute(
                "overdueCount",
                opsDossierService.getOverdueDossiers().size()
        );

        model.addAttribute(
                "nearlyDueCount",
                opsDossierService
                        .getNearlyDueAlerts(PageRequest.of(0, 1))
                        .getTotalElements()
        );


        // 3. Trạng thái đang chọn (để giữ filter)
        model.addAttribute("currentStatus", status);

        return "pages/admin/admin-records";
    }

    @GetMapping("/feedbacks")
    public String showFeedbacks(
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {

        model.addAttribute("feedbacks", modFeedbackService.getAllFeedbacks(
                PageRequest.of(page, 10)));
        model.addAttribute("unresolvedCount", modFeedbackService.unresolvedFeedbacks());

        return "pages/admin/admin-feedbacks";
    }
}