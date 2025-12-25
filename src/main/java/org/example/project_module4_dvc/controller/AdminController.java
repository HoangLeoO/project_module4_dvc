package org.example.project_module4_dvc.controller;

import lombok.RequiredArgsConstructor;
import org.example.project_module4_dvc.entity.cat.CatService;
import org.example.project_module4_dvc.service.cat.ICatServiceService;
import org.example.project_module4_dvc.service.iml.IDashboardAlertService;
import org.example.project_module4_dvc.service.mod.IModFeedbackService;
import org.example.project_module4_dvc.service.iml.ISysDepartmentService;
import org.example.project_module4_dvc.service.sys.ISysUserRoleService;
import org.example.project_module4_dvc.service.sys.ISysUserService;
import org.springframework.data.domain.PageRequest;
import org.example.project_module4_dvc.service.ops.IOpsDossierService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final IOpsDossierService opsDossierService;
    private final IModFeedbackService modFeedbackService;
    private final ISysDepartmentService sysDepartmentService;
    private final ICatServiceService catServiceService;
    private final IDashboardAlertService dashboardAlertService;
    private final ISysUserService sysUserService;
    private final ISysUserRoleService sysUserRoleService;


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
        model.addAttribute("onTimeRate", opsDossierService.calculateOnTimeRateStrict());
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

    @GetMapping("/services")
    public String listServices(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false, name = "new") Boolean isNew,
            Model model
    ) {
        model.addAttribute("services", catServiceService.findAll());

        if (Boolean.TRUE.equals(isNew)) {
            model.addAttribute("selectedService", new CatService());
        } else if (id != null) {
            model.addAttribute("selectedService",
                    catServiceService.findById(id).orElse(new CatService()));
        } else {
            model.addAttribute("selectedService", new CatService());
        }

        return "pages/admin/admin-services";
    }

    /**
     * Lưu (thêm / sửa)
     */
    @PostMapping("/services/save")
    public String saveService(@ModelAttribute CatService service) {
        catServiceService.save(service);
        return "redirect:/admin/services?id=" + service.getId();
    }

    /**
     * Xóa
     */
    @PostMapping("/services/delete/{id}")
    public String deleteService(@PathVariable("id") Long id) {
        catServiceService.deleteById(id);
        return "redirect:/admin/services";
    }

    @GetMapping("/users")
    public String list(Model model) {

        model.addAttribute("users", sysUserService.getOfficials());
        model.addAttribute("departments", sysDepartmentService.getAll());
        model.addAttribute("roles", sysUserRoleService.findAll());

        return "pages/admin/admin-users";
    }
}
