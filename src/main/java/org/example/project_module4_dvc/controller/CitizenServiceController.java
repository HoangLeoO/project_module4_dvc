package org.example.project_module4_dvc.controller;

import org.example.project_module4_dvc.config.CustomUserDetails;
import org.example.project_module4_dvc.dto.cat.CatServiceDTO;
import org.example.project_module4_dvc.entity.mock.MockCitizen;
import org.example.project_module4_dvc.service.cat.ICatServiceService;
import org.example.project_module4_dvc.service.mock.IMockCitizenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/citizen")
public class CitizenServiceController {

    @Autowired
    private ICatServiceService catServiceService;

    @Autowired
    private IMockCitizenService mockCitizenService;

    @Autowired
    private org.example.project_module4_dvc.service.sys.SysDepartmentService sysDepartmentService;

    @GetMapping("/services")
    public String showServices(@RequestParam(required = false) String keyword,
            @RequestParam(required = false) String domain,
            Model model) {
        List<CatServiceDTO> allServices;

        // Logical check: if keyword or domain is provided
        boolean hasKeyword = keyword != null && !keyword.isEmpty();
        boolean hasDomain = domain != null && !domain.isEmpty() && !"ALL".equals(domain);

        if (hasKeyword || hasDomain) {
            allServices = catServiceService.searchServices(keyword, domain);
        } else {
            allServices = catServiceService.getAllServices();
        }

        model.addAttribute("allServices", allServices);
        model.addAttribute("featuredServices", catServiceService.getFeaturedServices());
        model.addAttribute("activePage", "services");
        model.addAttribute("keyword", keyword);
        model.addAttribute("domain", domain);
        return "citizen/services/list";
    }

    @GetMapping("/service-detail")
    public String showServiceDetail(@RequestParam("code") String code, Model model) {
        var service = catServiceService.getServiceByCode(code);
        model.addAttribute("s", service);
        model.addAttribute("activePage", "services");
        return "citizen/services/detail";
    }

    @GetMapping("/submit-wizard")
    public String submitWizardRouter(@RequestParam("code") String code) {

        if (code == null || code.isBlank()) {
            return "redirect:/citizen/services";
        }

        return switch (code) {
            case "HK01_TRE" -> "redirect:/citizen/forms/hk01-tre";
            case "KD01_HKD" -> "redirect:/citizen/forms/kd01-hkd";
            case "HK02_KAITU" -> "redirect:/citizen/services/death/HK02_KAITU";
            case "HT02_XACNHANHN" -> "redirect:/citizen/tuphap/marital-status";
            default -> "redirect:/citizen/forms/not-supported?code=" + code;
        };
    }

}
