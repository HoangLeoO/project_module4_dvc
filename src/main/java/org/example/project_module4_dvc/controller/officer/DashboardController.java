package org.example.project_module4_dvc.controller.officer;

import org.example.project_module4_dvc.dto.dossier.NewDossierDTO;
import org.example.project_module4_dvc.entity.ops.OpsDossierFile;
import org.example.project_module4_dvc.service.officer.IOfficerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/officer/dashboard")
public class DashboardController {
    private final IOfficerService officerService;

    public DashboardController(IOfficerService officerService) {
        this.officerService = officerService;
    }

    @GetMapping("")
    public String getDossierList(Model model, @PageableDefault(size = 5) Pageable pageable) {
        Page<NewDossierDTO> page = officerService.findAll("NEW", pageable);
        List<NewDossierDTO> nearDueList = officerService.findNearlyDue();
        model.addAttribute("nearDueCount", nearDueList.size());
        model.addAttribute("dossiers", page);
        return "pages/officer/officer-dashboard";
    }

    @GetMapping("/reception")
    public String getReceptionForm(Model model, @RequestParam("id") Long id) {
        List<OpsDossierFile> opsDossierFile = officerService.findFileByDossierId(id);
        NewDossierDTO newDossierDTO = officerService.findById(id);
        model.addAttribute("dossier", newDossierDTO);
        model.addAttribute("files", opsDossierFile);
        return "pages/officer/officer-reception";
    }

    @GetMapping("reception/update")
    public String updateDossierStatus(@RequestParam("id") Long id) {
        officerService.updateDossierStatus(id, "PENDING","");
        return "redirect:/officer/dashboard";
    }

    @PostMapping("reception/reject")
    public String rejectDossierStatus(@RequestParam("id") Long id, @RequestParam("reason") String reason) {
        officerService.updateDossierStatus(id, "REJECTED",reason);
        return "redirect:/officer/dashboard";
    }


}
