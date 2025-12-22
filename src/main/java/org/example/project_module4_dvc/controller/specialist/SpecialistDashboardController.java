package org.example.project_module4_dvc.controller.specialist;

import org.example.project_module4_dvc.config.CustomUserDetails;
import org.example.project_module4_dvc.dto.dossier.NewDossierDTO;
import org.example.project_module4_dvc.service.officer.IOfficerService;
import org.example.project_module4_dvc.service.specialist.ISpecialistService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/specialist/dashboard")
public class SpecialistDashboardController {
    private final IOfficerService officerService;
    private final ISpecialistService specialistService;


    public SpecialistDashboardController(IOfficerService officerService, ISpecialistService specialistService) {
        this.officerService = officerService;
        this.specialistService = specialistService;
    }

    @GetMapping("")
    public String getDossierList(Model model, @PageableDefault(size = 5,
                                         sort = "submissionDate",
                                         direction = Sort.Direction.ASC) Pageable pageable,
                                 @AuthenticationPrincipal CustomUserDetails userDetails) {
        model.addAttribute("officerName", userDetails.getFullName());
        model.addAttribute("specialistId", userDetails.getUserId());
        model.addAttribute("departmentName", userDetails.getDepartmentName());
        System.out.println(userDetails.getDepartmentName());
        Page<NewDossierDTO> page = specialistService.findAll("PENDING", userDetails.getDepartmentName(), userDetails.getUserId(), pageable);
//        List<NewDossierDTO> nearDueList = officerService.findNearlyDue(userDetails.getDepartmentName());
//        model.addAttribute("nearDueCount", nearDueList.size());
        model.addAttribute("dossiers", page);


        return "pages/specialist/specialist-dashboard";
    }
}
