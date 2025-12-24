package org.example.project_module4_dvc.controller.officer;

import org.example.project_module4_dvc.config.CustomUserDetails;
import org.example.project_module4_dvc.dto.dossier.NewDossierDTO;
import org.example.project_module4_dvc.service.officer.IOfficerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/officer/dashboard/return")
public class ReturnController {
    private final IOfficerService officerService;

    public ReturnController(IOfficerService officerService) {
        this.officerService = officerService;
    }

    @GetMapping("")
    public String getReturnForm(Model model, @PageableDefault(size = 5) Pageable pageable,
                                @AuthenticationPrincipal CustomUserDetails userDetails) {
        model.addAttribute("officerName", userDetails.getFullName());
        model.addAttribute("departmentName", userDetails.getDepartmentName());
        Page<NewDossierDTO> page = officerService.findAll("APPROVED", userDetails.getDepartmentName(), pageable);
        model.addAttribute("dossiers", page);
        return "pages/officer/officer-return";
    }

    @GetMapping("/result")
    public String returnDossier(@RequestParam("id") Long id) {
        NewDossierDTO dossier = officerService.findById(id);
        officerService.updateDossierStatus(id, "RESULT_RETURNED", dossier.getSpecialistId(), dossier.getDueDate(), dossier.getRejectionReason());
        return "redirect:/officer/dashboard/return";

    }
}