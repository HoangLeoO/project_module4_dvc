package org.example.project_module4_dvc.controller;

import org.example.project_module4_dvc.config.CustomUserDetails;
import org.example.project_module4_dvc.service.cat.ICatServiceService;
import org.example.project_module4_dvc.service.mock.IMockCitizenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/citizen/forms")
public class BirthCertificateController {
    @Autowired private ICatServiceService catServiceService;
    @Autowired private IMockCitizenService mockCitizenService;
    @Autowired private org.example.project_module4_dvc.service.sys.SysDepartmentService sysDepartmentService;

    @GetMapping("/hk01-tre")
    public String showForm(Model model, Authentication authentication) {

        String code = "HK01_TRE";
        var service = catServiceService.getServiceByCode(code);
        model.addAttribute("s", service);
        model.addAttribute("serviceCode", code);

        CustomUserDetails ud = (CustomUserDetails) authentication.getPrincipal();
        Long citizenId = ud.getCitizenId();

        var citizen = mockCitizenService.findById(citizenId);
        model.addAttribute("citizen", citizen);

        var spouse = mockCitizenService.findSpouseByCitizenId(citizenId);
        if (spouse != null) model.addAttribute("spouse", spouse);

        model.addAttribute("sysDepartments", sysDepartmentService.getAll());
        model.addAttribute("activePage", "services");

        return "citizen/portal-submit-wizard";
    }
}
