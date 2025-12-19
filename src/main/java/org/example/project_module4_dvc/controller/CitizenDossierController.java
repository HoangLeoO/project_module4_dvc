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
import org.example.project_module4_dvc.repository.mock.MockCitizenRepository;
import org.example.project_module4_dvc.repository.mod.ModPersonalVaultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.example.project_module4_dvc.config.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    private OpsDossierLogRepository dossierLogRepository;
    @Autowired
    private MockCitizenRepository citizenRepository;
    @Autowired
    private ModPersonalVaultRepository vaultRepository;

    // 1. TRANG DASHBOARD / HỒ SƠ CỦA TÔI
    @GetMapping("/hoso")
    public String showDossierList(Model model,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Long userId = userDetails.getUserId();

        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "submissionDate"));
        Page<OpsDossierSummaryDTO> dossierPage = opsDossierService.getMyDossierList(userId, pageable);

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
        return "citizen/dashboard";
    }

    @GetMapping("/dashboard")
    public String showDashBoard() {
        return "redirect:/citizen/hoso";
    }

    // 2. TRANG CHI TIẾT HỒ SƠ
    @GetMapping("/hoso/{id}")
    public String showDossierDetail(@PathVariable("id") Long id, Model model) {
        OpsDossierDetailDTO detail = opsDossierService.getDossierDetail(id);
        List<IDossierLogProjectionDTO> history = dossierLogRepository.findLogsByDossierId(id);

        model.addAttribute("history", history);
        model.addAttribute("currentDossierId", id);
        model.addAttribute("d", detail);
        model.addAttribute("activePage", "hoso");
        return "citizen/dossier/detail";
    }

    // 3. TRANG DANH SÁCH TẤT CẢ HỒ SƠ
    @GetMapping("/hoso/danh-sach")
    public String showFullDossierList(Model model,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status) {
        Long userId = userDetails.getUserId();
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "submissionDate"));

        Page<OpsDossierSummaryDTO> dossierPage = opsDossierService.searchMyDossiers(userId, keyword, status, pageable);

        model.addAttribute("activePage", "hoso");
        model.addAttribute("dossiers", dossierPage.getContent());
        model.addAttribute("dossierPage", dossierPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        return "citizen/dossier/list";
    }

    // 4. CÁC TRANG BỔ SUNG KHÁC
    @GetMapping("/profile")
    public String showProfile(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Long citizenId = userDetails.getCitizenId();
        if (citizenId != null) {
            model.addAttribute("citizen", citizenRepository.findById(citizenId).orElse(null));
        }
        model.addAttribute("activePage", "profile");
        return "citizen/profile";
    }

    @GetMapping("/vault")
    public String showVault(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Long userId = userDetails.getUserId();
        model.addAttribute("vaultItems", vaultRepository.findByUserId(userId));
        model.addAttribute("activePage", "vault");
        return "citizen/vault";
    }

    @GetMapping("/feedback")
    public String showFeedback(Model model) {
        model.addAttribute("activePage", "feedback");
        return "citizen/feedback";
    }

    @GetMapping("/tracking")
    public String showTracking(Model model) {
        model.addAttribute("activePage", "tracking");
        return "citizen/tracking";
    }
}
