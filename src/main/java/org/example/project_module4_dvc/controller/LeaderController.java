package org.example.project_module4_dvc.controller;

import org.example.project_module4_dvc.dto.leader.DossierApprovalSummaryDTO;
import org.example.project_module4_dvc.entity.sys.SysUser;
import org.example.project_module4_dvc.service.cat.ICatServiceService;
import org.example.project_module4_dvc.service.ILeaderService;
import org.example.project_module4_dvc.service.sys.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/leader/")
public class LeaderController {

    private final ILeaderService leaderService;

    @Autowired
    private ICatServiceService catServiceService;

    @Autowired
    private ISysUserService userService;

    public LeaderController(ILeaderService leaderService){
        this.leaderService = leaderService;
    }

    @GetMapping("dashboard")
    public String showDashboard(Model model, Principal principal){
        try {
            SysUser sysUser = userService.findByUsername(principal.getName());
            long delegatedCount = leaderService.countDelegatedDossiers(sysUser.getId(), "VERIFIED");
            model.addAttribute("delegatedCount", delegatedCount);
        } catch (Exception e) {
            model.addAttribute("delegatedCount", 0);
        }
        return "pages/04-leader/leader-dashboard";
    }

    @GetMapping("reports")
    public String showReports(){
        return "pages/04-leader/leader-reports";
    }


    @GetMapping("delegation")
    public String showDelegation(){
        return "pages/04-leader/leader-delegation";
    }


    @GetMapping("my-dossiers")
    public String showListMyDossier(
            @RequestParam(name = "size",required = false,defaultValue = "3") int size,
            @RequestParam(name = "page",required = false,defaultValue = "0") int page,
            @RequestParam(name = "domain",required = false) String domain,
            @RequestParam(name = "applicantName",required = false) String applicantName,
            Model model,
            Principal principal
    ){
        // Xử lý chuỗi rỗng thành null để query hoạt động đúng
        if (domain != null && domain.trim().isEmpty()) domain = null;
        if (applicantName != null && applicantName.trim().isEmpty()) applicantName = null;

        Pageable pageable = PageRequest.of(page,size);
        SysUser sysUser = userService.findByUsername(principal.getName());
        Long idLeader = sysUser.getId();
        Page<DossierApprovalSummaryDTO> dossiers = leaderService.getMyDossiers(idLeader,applicantName,domain,pageable);
        
        model.addAttribute("dossiers", dossiers);
        model.addAttribute("catServices", catServiceService.findAll());
        model.addAttribute("viewType", "mine"); // Đánh dấu là tab "Của tôi"
        return "pages/04-leader/leader-approval";
    }

    @GetMapping("delegated-dossiers")
    public String showDelegatedDossiers(
            @RequestParam(name = "size",required = false,defaultValue = "3") int size,
            @RequestParam(name = "page",required = false,defaultValue = "0") int page,
            @RequestParam(name = "domain",required = false) String domain,
            @RequestParam(name = "applicantName",required = false) String applicantName,
            Model model
    ){
        // Xử lý chuỗi rỗng thành null
        if (domain != null && domain.trim().isEmpty()) domain = null;
        if (applicantName != null && applicantName.trim().isEmpty()) applicantName = null;

        Pageable pageable = PageRequest.of(page,size);
        // ID 3L là mock cho Lãnh đạo hiện tại
        Page<DossierApprovalSummaryDTO> dossiers = leaderService.getDelegatedDossiers(3L, applicantName, domain, pageable);
        
        model.addAttribute("dossiers", dossiers);
        model.addAttribute("catServices", catServiceService.findAll());
        model.addAttribute("viewType", "delegated"); // Đánh dấu là tab "Được ủy quyền"
        return "pages/04-leader/leader-approval";
    }

    @PostMapping("approval/{dossiersId}")
    public String approval(@PathVariable(name = "dossiersId",required = false) Long dossiersId, RedirectAttributes redirectAttributes){
        leaderService.approvedByLeader(2L,dossiersId);
        redirectAttributes.addFlashAttribute("mess","Phê duyệt thành công!");
        return "redirect:/leader/my-dossiers";
    }
}
