package org.example.project_module4_dvc.controller.officer;

import org.example.project_module4_dvc.service.officer.IOfficerService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/officer/dashboard/return")
public class ReturnController {
    private final IOfficerService officerService;

    public ReturnController(IOfficerService officerService) {
        this.officerService = officerService;
    }

    @GetMapping("")
    public String getReturnForm(Model model, @PageableDefault(size = 5) Pageable pageable) {
        model.addAttribute("dossiers", officerService.findAllResult("APPROVED",pageable));
        return "pages/officer/officer-return";
    }
}
