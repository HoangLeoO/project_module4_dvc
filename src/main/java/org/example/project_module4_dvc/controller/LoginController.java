package org.example.project_module4_dvc.controller;


import org.example.project_module4_dvc.service.sys.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/")
public class LoginController {

    @Autowired
    private ISysUserService userService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String landingPage() {
        return "citizen/home";
    }

    @RequestMapping(value = "login/citizen", method = RequestMethod.GET)
    public String loginPageCitizen() {
        return "citizen/login";
    }

    @RequestMapping(value = "login/official", method = RequestMethod.GET)
    public String loginPageOfficial() {
        return "pages/official-login";
        // Trang login mới dành cho cán bộ
    }

    @RequestMapping(value = "login/officer", method = RequestMethod.GET)
    public String loginPageOfficer() {
        return "pages/official-login";
    }

    @RequestMapping(value = "login/specialist", method = RequestMethod.GET)
    public String loginPageSpecialist() {
        return "pages/official-login";
    }

    @RequestMapping(value = "/403", method = RequestMethod.GET)
    public String accessDenied() {
        return "pages/403";
    }

    @RequestMapping(value = "/404", method = RequestMethod.GET)
    public String notFound() {
        return "pages/404";
    }
}
