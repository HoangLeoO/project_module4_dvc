package org.example.project_module4_dvc.controller.leader;

import org.example.project_module4_dvc.dto.leader.DelegationConfigDTO;
import org.example.project_module4_dvc.dto.leader.DossierApprovalSummaryDTO;
import org.example.project_module4_dvc.entity.sys.SysUser;
import org.example.project_module4_dvc.service.cat.ICatServiceService;
import org.example.project_module4_dvc.service.learder.ILeaderService;
import org.example.project_module4_dvc.service.pdf.PdfService;
import org.example.project_module4_dvc.service.sys.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.project_module4_dvc.entity.ops.OpsDossier;
import org.example.project_module4_dvc.repository.ops.OpsDossierRepository;
import org.example.project_module4_dvc.entity.cat.CatService;
import org.example.project_module4_dvc.entity.ops.OpsDossierResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/leader/")
public class LeaderController {

    private final ILeaderService leaderService;

    @Autowired
    private ICatServiceService catServiceService;

    @Autowired
    private ISysUserService userService;

    @Autowired
    private OpsDossierRepository opsDossierRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PdfService pdfService;

    private SimpMessagingTemplate messagingTemplate;

    public LeaderController(ILeaderService leaderService, SimpMessagingTemplate messagingTemplate){
        this.leaderService = leaderService;
        this.messagingTemplate = messagingTemplate;
    }

    @ModelAttribute("pendingCount")
    public long getPendingCount(Principal principal) {
        if (principal == null) return 0;
        try {
            SysUser sysUser = userService.findByUsername(principal.getName());
            return leaderService.countByCurrentHandler_IdAndDossierStatus(sysUser.getId(), "VERIFIED");
        } catch (Exception e) {
            return 0;
        }
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
    public String showReports(
            @RequestParam(name = "periodType", defaultValue = "MONTH") String periodType,
            @RequestParam(name = "year", required = false) Integer year,
            @RequestParam(name = "periodValue", required = false) Integer periodValue,
            Model model,
            Principal principal
    ) {
        SysUser sysUser = userService.findByUsername(principal.getName());
        Long deptId = (sysUser.getDepartment() != null) ? sysUser.getDepartment().getId() : 0L; // Fallback if no dept

        // Defaults
        if (year == null) year = java.time.LocalDate.now().getYear();
        if (periodValue == null && "MONTH".equalsIgnoreCase(periodType)) {
            periodValue = java.time.LocalDate.now().getMonthValue();
        }

        // 1. Get Summary Stats (Top Cards)
        org.example.project_module4_dvc.dto.leader.report.ReportSummaryDTO summary = 
                leaderService.getReportSummary(deptId, periodType, year, periodValue);
        model.addAttribute("summary", summary);

        // 2. Get Domain Stats (Table)
        java.util.List<org.example.project_module4_dvc.dto.leader.report.ReportDomainStatDTO> domainStats = 
                leaderService.getDomainStats(deptId, periodType, year, periodValue);
        model.addAttribute("domainStats", domainStats);

        // 3. Pass Filter Params back to view
        model.addAttribute("periodType", periodType);
        model.addAttribute("year", year);
        model.addAttribute("periodValue", periodValue);

        return "pages/04-leader/leader-reports";
    }


    @GetMapping("profile")
    public String showProfile(Model model, Principal principal){
        SysUser sysUser = userService.findByUsername(principal.getName());
        model.addAttribute("user", sysUser);
        return "pages/04-leader/leader-profile";
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

        // Generate PDF
        try {
            OpsDossier dossier = opsDossierRepository.findById(dossiersId).orElse(null);
            if (dossier != null) {
                String pdfPath = pdfService.generateSignedDossierPdf(dossier, sysUser.getFullName());
                System.out.println("PDF Signed: " + pdfPath);

                // Save Result
                OpsDossierResult result = OpsDossierResult.builder()
                        .dossier(dossier)
                        .decisionNumber("QD-" + dossier.getDossierCode())
                        .signerName(sysUser.getFullName())
                        .eFileUrl("/uploads/pdf/" + new File(pdfPath).getName())
                        .build();
                leaderService.opsDossierResults(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Don't block flow, just log
        }

        messagingTemplate.convertAndSend("/topic/dossiers/returned", "returned");
        redirectAttributes.addFlashAttribute("mess","Phê duyệt thành công!");
        return "redirect:/leader/my-dossiers";
    }

    @GetMapping("dossiers/{id}/detail")
    public String getDossierDetail(@PathVariable Long id, Model model) {
        OpsDossier dossier = opsDossierRepository.findById(id).orElse(null);
        if (dossier == null) {
            return "components/common/no-data"; // Or a simple 404 text
        }

        String serviceCode = dossier.getService().getServiceCode();
        String fragmentPath = "";

        // Map serviceCode to fragment path
        switch (serviceCode) {
            case "HK01_TRE": // Birth
                fragmentPath = "components/form/preview/birth-registration :: form-detail";
                break;
            case "HK02_KAITU": // Death
                fragmentPath = "components/form/preview/death-registration :: form-detail";
                break;
            case "HT01_KETHON": // Marriage
                fragmentPath = "components/form/preview/marriage-registration :: form-detail";
                break;
            case "HT02_XACNHANHN": // Marital Status
                fragmentPath = "components/form/preview/marital-status-certificate :: form-detail";
                break;
            case "DD01_BIENDONG": // Land Change
                fragmentPath = "components/form/preview/land-change-registration :: form-detail";
                break;
            case "DD02_CHUYENMDSD": // Land Purpose Change
                fragmentPath = "components/form/preview/land-purpose-change :: form-detail";
                break;
            case "DD03_TACHHOP": // Land Split/Merge
                fragmentPath = "components/form/preview/land-split-merge :: form-detail";
                break;
            case "KD01_HKD": // Household Business
                fragmentPath = "components/form/preview/household-business-registration :: form-detail";
                break;
            default:
                // Default fallback if unknown service
                 return "components/common/no-data";
        }

        try {
            if (dossier.getFormData() != null) {
                Object rawForm = dossier.getFormData();
                if (rawForm instanceof String) {
                     model.addAttribute("formData", objectMapper.readValue((String) rawForm, Map.class));
                } else {
                     model.addAttribute("formData", rawForm);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return fragmentPath;
    }
}
