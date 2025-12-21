package org.example.project_module4_dvc.controller.leader;

import org.example.project_module4_dvc.dto.leader.DelegationConfigDTO;
import org.example.project_module4_dvc.dto.leader.DossierApprovalSummaryDTO;
import org.example.project_module4_dvc.entity.sys.SysUser;
import org.example.project_module4_dvc.service.cat.ICatServiceService;
import org.example.project_module4_dvc.service.learder.ILeaderService;
import org.example.project_module4_dvc.service.sys.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.example.project_module4_dvc.entity.cat.CatService;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

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
            
            // 1. Delegated Count
            long delegatedCount = leaderService.countDelegatedDossiers(sysUser.getId(), "VERIFIED");
            model.addAttribute("delegatedCount", delegatedCount);

            // 2. Pending Approval Count (My Dossiers)
            long pendingCount = leaderService.countByCurrentHandler_IdAndDossierStatus(sysUser.getId(), "VERIFIED");
            model.addAttribute("pendingCount", pendingCount);

            // 3. On-time Rate
            Double onTimeRate = 0.0;
            if (sysUser.getDepartment() != null) {
                Long deptId = sysUser.getDepartment().getId();
                onTimeRate = leaderService.getOnTimeRateByDeptId(deptId);
                
                // 4. Overdue Count
                long overdueCount = leaderService.countOverdueDossiersByDept(deptId);
                model.addAttribute("overdueCount", overdueCount);

                // 5. Satisfaction Score
                Double satisfactionScore = leaderService.getAverageSatisfactionScoreByDept(deptId);
                // Round to 1 decimal place
                satisfactionScore = Math.round(satisfactionScore * 10.0) / 10.0;
                model.addAttribute("satisfactionScore", satisfactionScore);
            } else {
                 model.addAttribute("overdueCount", 0);
                 model.addAttribute("satisfactionScore", 0.0);
            }
            // Handle null if no data
            if (onTimeRate == null) onTimeRate = 100.0; 
            model.addAttribute("onTimeRate", onTimeRate);

        } catch (Exception e) {
            model.addAttribute("delegatedCount", 0);
            model.addAttribute("pendingCount", 0);
            model.addAttribute("onTimeRate", 100.0);
        }
        return "pages/04-leader/leader-dashboard";
    }

    @GetMapping("reports")
    public String showReports(){
        return "pages/04-leader/leader-reports";
    }


    @GetMapping("delegation")
    public String showDelegation(Model model, Principal principal){
        SysUser sysUser = userService.findByUsername(principal.getName());
        Long leaderId = sysUser.getId();

        // 1. Get Config Data (Delegatees, Current Delegations)
        DelegationConfigDTO config = leaderService.getDelegationConfigData(leaderId);
        model.addAttribute("delegatees", config.getPotentialDelegatees());
        model.addAttribute("delegations", config.getCurrentDelegations());
        
        // 2. Get Services/Domains for Scope Selection
        // model.addAttribute("catServices", catServiceService.findAll());
        java.util.List<String> domains = catServiceService.findAll().stream()
                .map(org.example.project_module4_dvc.entity.cat.CatService::getDomain)
                .distinct()
                .toList();
        model.addAttribute("domains", domains);

        return "pages/04-leader/leader-delegation";
    }

    @PostMapping("delegation/create")
    public String createDelegation(org.example.project_module4_dvc.dto.leader.DelegationRequestDTO request, 
                                   RedirectAttributes redirectAttributes, 
                                   Principal principal) {
        SysUser sysUser = userService.findByUsername(principal.getName());
        try {
            leaderService.createDelegation(sysUser.getId(), request);
            redirectAttributes.addFlashAttribute("mess", "Thiết lập ủy quyền thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/leader/delegation";
    }

    @PostMapping("delegation/revoke/{id}")
    public String revokeDelegation(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            leaderService.revokeDelegation(id);
            redirectAttributes.addFlashAttribute("mess", "Đã thu hồi ủy quyền!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/leader/delegation";
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
        model.addAttribute("dossiers", dossiers);
        // Lấy danh sách domain không trùng lặp
        List<String> domains = catServiceService.findAll().stream()
                .map(CatService::getDomain)
                .distinct()
                .toList();
        model.addAttribute("domains", domains);
        model.addAttribute("viewType", "mine"); // Đánh dấu là tab "Của tôi"
        return "pages/04-leader/leader-approval";
    }

    @GetMapping("delegated-dossiers")
    public String showDelegatedDossiers(
            @RequestParam(name = "size",required = false,defaultValue = "3") int size,
            @RequestParam(name = "page",required = false,defaultValue = "0") int page,
            @RequestParam(name = "domain",required = false) String domain,
            @RequestParam(name = "applicantName",required = false) String applicantName,
            Model model,
            Principal principal
    ){
        // Xử lý chuỗi rỗng thành null
        if (domain != null && domain.trim().isEmpty()) domain = null;
        if (applicantName != null && applicantName.trim().isEmpty()) applicantName = null;

        Pageable pageable = PageRequest.of(page,size);
        SysUser sysUser = userService.findByUsername(principal.getName());
        Long leaderId = sysUser.getId();

        Page<DossierApprovalSummaryDTO> dossiers = leaderService.getDelegatedDossiers(leaderId, applicantName, domain, pageable);
        
        model.addAttribute("dossiers", dossiers);
        model.addAttribute("dossiers", dossiers);
        // Lấy danh sách domain không trùng lặp
        List<String> domains = catServiceService.findAll().stream()
                .map(CatService::getDomain)
                .distinct()
                .toList();
        model.addAttribute("domains", domains);
        model.addAttribute("viewType", "delegated"); // Đánh dấu là tab "Được ủy quyền"
        return "pages/04-leader/leader-approval";
    }

    @GetMapping("history")
    public String showHistory(
            @RequestParam(name = "size",required = false,defaultValue = "3") int size,
            @RequestParam(name = "page",required = false,defaultValue = "0") int page,
            @RequestParam(name = "domain",required = false) String domain,
            @RequestParam(name = "applicantName",required = false) String applicantName,
            Model model,
            Principal principal
    ){
        // Xử lý chuỗi rỗng thành null
        if (domain != null && domain.trim().isEmpty()) domain = null;
        if (applicantName != null && applicantName.trim().isEmpty()) applicantName = null;

        Pageable pageable = PageRequest.of(page,size);
        SysUser sysUser = userService.findByUsername(principal.getName());
        Long leaderId = sysUser.getId();

        // Gọi service tìm lịch sử
        Page<DossierApprovalSummaryDTO> dossiers = leaderService.findApprovedHistory(leaderId, applicantName, domain, pageable);

        model.addAttribute("dossiers", dossiers);
        model.addAttribute("dossiers", dossiers);
        // Lấy danh sách domain không trùng lặp
        List<String> domains = catServiceService.findAll().stream()
                .map(CatService::getDomain)
                .distinct()
                .toList();
        model.addAttribute("domains", domains);
        model.addAttribute("viewType", "history"); // Đánh dấu là tab "Lịch sử"
        return "pages/04-leader/leader-approval";
    }

    @PostMapping("approval/{dossiersId}")
    public String approval(@PathVariable(name = "dossiersId",required = false) Long dossiersId, RedirectAttributes redirectAttributes,Principal principal){
        SysUser sysUser = userService.findByUsername(principal.getName());
        System.out.println(sysUser.getId());
        leaderService.approvedByLeader(sysUser.getId(),dossiersId);
        redirectAttributes.addFlashAttribute("mess","Phê duyệt thành công!");
        return "redirect:/leader/my-dossiers";
    }
}
