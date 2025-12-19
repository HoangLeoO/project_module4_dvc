package org.example.project_module4_dvc.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.project_module4_dvc.dto.OpsDossierDTO.OpsDossierDetailDTO;
import org.example.project_module4_dvc.dto.OpsDossierDTO.OpsDossierSummaryDTO;
import org.example.project_module4_dvc.dto.timeline.IDossierLogProjectionDTO;
import org.example.project_module4_dvc.dto.timeline.IWorkflowStepProjectionDTO;
import org.example.project_module4_dvc.repository.ops.OpsDossierLogRepository;
import org.example.project_module4_dvc.service.cat.ICatWorkflowStepService;
import org.example.project_module4_dvc.service.ops.IOpsDossierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/citizen")
@RequiredArgsConstructor
public class CitizenDossierController {
    @Autowired
    private IOpsDossierService opsDossierService;
    @Autowired
    private ICatWorkflowStepService workflowStepService;
    @Autowired
    private OpsDossierLogRepository dossierLogRepository;

    /**
     * Hàm hỗ trợ lấy ID người dùng hiện tại.
     * Hiện tại: Trả về 1.
     * Tương lai: Lấy từ session.
     */
    private Long getCurrentUserId(HttpSession session) {
        // Sau này làm Login xong thì dùng dòng này:
        // Long userId = (Long) session.getAttribute("userId");
        // return (userId != null) ? userId : 0L;

        return 8L; // Fix cứng ID = 1 để làm giao diện
    }

    // 1. TRANG DANH SÁCH HỒ SƠ
    @GetMapping("/hoso")
    public String showDossierList(Model model,
            HttpSession session,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Long userId = getCurrentUserId(session);

        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "submissionDate"));
        Page<OpsDossierSummaryDTO> dossierPage = opsDossierService.getMyDossierList(userId, pageable);

        // stats (nếu bạn đang dùng dashboard)
        Map<String, Long> statusCounts = opsDossierService.getStatusCountByUser(userId);

        model.addAttribute("dossiers", dossierPage.getContent());
        model.addAttribute("dossierPage", dossierPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);

        model.addAttribute("newCount", statusCounts.getOrDefault("NEW", 0L));
        model.addAttribute("processingCount", statusCounts.getOrDefault("PENDING", 0L));
        model.addAttribute("supplementCount", statusCounts.getOrDefault("NEED_SUPPLEMENT", 0L));
        model.addAttribute("completedCount", statusCounts.getOrDefault("APPROVED", 0L));
        model.addAttribute("notificationsTop3", opsDossierService.getTop3MyNotifications(userId));
        model.addAttribute("activePage", "hoso");
        return "pages/01-citizen-portal/portal-dashboard";
    }

    @GetMapping("/dashboard")
    public String showDashBoard(Model model) {
        return "pages/01-citizen-portal/portal-tracking";
    }

    // 2. TRANG CHI TIẾT HỒ SƠ
    @GetMapping("/hoso/{id}")
    public String showDossierDetail(@PathVariable("id") Long id, Model model) {
        // Lấy chi tiết hồ sơ từ Service
        OpsDossierDetailDTO detail = opsDossierService.getDossierDetail(id);
        List<IWorkflowStepProjectionDTO> steps = workflowStepService.getWorkflowSteps(id);
        List<IDossierLogProjectionDTO> history = dossierLogRepository.findLogsByDossierId(id);

        model.addAttribute("steps", steps);
        model.addAttribute("history", history);
        model.addAttribute("currentDossierId", id);
        model.addAttribute("d", detail);
        model.addAttribute("activePage", "hoso");
        return "pages/01-citizen-portal/detail-dossier";
    }

    // 3. TRANG DANH SÁCH TẤT CẢ HỒ SƠ
    @GetMapping("/hoso/danh-sach")
    public String showFullDossierList(Model model,
            HttpSession session,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status) {
        Long userId = getCurrentUserId(session);
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "submissionDate"));

        Page<OpsDossierSummaryDTO> dossierPage = opsDossierService.searchMyDossiers(userId, keyword, status, pageable);

        model.addAttribute("activePage", "hoso");
        model.addAttribute("dossiers", dossierPage.getContent());
        model.addAttribute("dossierPage", dossierPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        return "pages/01-citizen-portal/list-dossier";
    }
}
