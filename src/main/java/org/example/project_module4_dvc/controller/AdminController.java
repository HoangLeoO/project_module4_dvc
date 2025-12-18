package org.example.project_module4_dvc.controller;

import lombok.RequiredArgsConstructor;
import org.example.project_module4_dvc.service.iml.IModFeedbackService;
import org.example.project_module4_dvc.service.iml.IOpsDossierService;
import org.example.project_module4_dvc.service.iml.ISysDepartmentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
    @RequestMapping("/admins")
    @RequiredArgsConstructor
    public class AdminController {
        private final IOpsDossierService opsDossierService;
        private final IModFeedbackService modFeedbackService;
        private final ISysDepartmentService sysDepartmentService;
    @GetMapping("")
    public String showDashboard(Model model) {
        model.addAttribute("summary", opsDossierService.getSummary());
        model.addAttribute("chartData", opsDossierService.getChartData());
        model.addAttribute("totalFeedbacks", modFeedbackService.totalFeedbacks());
        model.addAttribute("resolvedFeedbacks", modFeedbackService.resolvedFeedbacks());

        model.addAttribute("unresolvedFeedbacks", modFeedbackService.unresolvedFeedbacks());

        // Lấy danh sách cảnh báo
        List<Map<String, Object>> dossierAlerts = opsDossierService.getDossierAlerts();
        List<Map<String, Object>> overdueDossiers = dossierAlerts.stream()
                .filter(d -> "OVERDUE".equals(d.get("type")))
                .toList();
        List<Map<String, Object>> nearlyDueDossiers = dossierAlerts.stream()
                .filter(d -> "NEARLY_DUE".equals(d.get("type")))
                .toList();

        model.addAttribute("overdueDossiers", overdueDossiers);
        model.addAttribute("almostDueDossiers", nearlyDueDossiers);

        return "admin/dashboard";
    }



}
