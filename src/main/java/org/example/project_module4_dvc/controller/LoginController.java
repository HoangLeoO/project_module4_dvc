package org.example.project_module4_dvc.controller;

import org.example.project_module4_dvc.service.ISysUserService;
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
        return "redirect:/login/citizen";
    }

    @RequestMapping(value = "/login/citizen", method = RequestMethod.GET)
    public String loginPageCitizen() {
        return "pages/login"; // Trang login hiện tại (dành cho công dân)
    }

    @RequestMapping(value = "/login/official", method = RequestMethod.GET)
    public String loginPageOfficial() {
        return "pages/official-login"; // Trang login mới dành cho cán bộ
    }

}
