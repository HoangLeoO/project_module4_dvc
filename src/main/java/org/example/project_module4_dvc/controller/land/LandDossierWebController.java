package org.example.project_module4_dvc.controller.land;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.project_module4_dvc.dto.landConversion.*;
import org.example.project_module4_dvc.entity.ops.OpsDossier;
import org.example.project_module4_dvc.entity.ops.OpsDossierLog;
import org.example.project_module4_dvc.entity.sys.SysUser;
import org.example.project_module4_dvc.repository.ops.OpsDossierLogRepository;
import org.example.project_module4_dvc.repository.ops.OpsDossierRepository;
import org.example.project_module4_dvc.repository.sys.SysUserRepository;
import org.example.project_module4_dvc.service.learder.LandConversionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/land")
public class LandDossierWebController {
    @Autowired
    private LandConversionService landConversionService;
    @Autowired
    private OpsDossierRepository dossierRepository;
    @Autowired
    private OpsDossierLogRepository logRepository;
    @Autowired
    private SysUserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;

    // ==================== BƯỚC 2: TIẾP NHẬN ====================
    /**
     * Trang danh sách hồ sơ đất cần tiếp nhận
     * GET /land/reception
     */
    @GetMapping("/reception")
    public String showReceptionList(Model model, Principal principal) {
        // Lấy hồ sơ đất đai với status = NEW
        List<OpsDossier> dossiers = dossierRepository
                .findByDossierStatusAndServiceServiceCodeStartingWith("NEW", "DD");

        model.addAttribute("dossiers", dossiers);
        return "pages/land/reception-list";
    }

    /**
     * Trang chi tiết tiếp nhận hồ sơ
     * GET /land/reception/{id}
     */
    @GetMapping("/reception/{id}")
    public String showReceptionDetail(@PathVariable Long id, Model model) {
        OpsDossier dossier = dossierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ"));

        // Lấy danh sách cán bộ địa chính để gán
        // TODO: Tạm thời lấy tất cả user, cần filter theo role sau
        List<SysUser> landOfficers = userRepository.findAll().stream()
                .filter(u -> "OFFICIAL".equals(u.getUserType()))
                .toList();

        model.addAttribute("dossier", dossier);
        model.addAttribute("landOfficers", landOfficers);

        return "pages/land/reception-detail";
    }

    /**
     * Xử lý tiếp nhận hồ sơ
     * POST /land/reception/submit
     */
    @PostMapping("/reception/submit")
    public String submitReception(
            @RequestParam Long dossierId,
            @RequestParam String action, // ACCEPT, REJECT, REQUIRE_SUPPLEMENT
            @RequestParam(required = false) Long nextHandlerId,
            @RequestParam String comments,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        try {
            SysUser currentUser = userRepository.findByUsername(principal.getName());

            DossierReceiveDTO dto = new DossierReceiveDTO();
            dto.setDossierId(dossierId);
            dto.setHandlerId(currentUser.getId());
            dto.setAction(action);
            dto.setNextHandlerId(nextHandlerId);
            dto.setComments(comments);

            landConversionService.receiveDossier(dto);

            redirectAttributes.addFlashAttribute("message", "Tiếp nhận hồ sơ thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }

        return "redirect:/land/reception";
    }

    // ==================== BƯỚC 3: THẨM ĐỊNH ====================
    /**
     * Trang danh sách hồ sơ cần thẩm định
     * GET /land/appraisal
     */
    @GetMapping("/appraisal")
    public String showAppraisalList(Model model, Principal principal) {
        SysUser currentUser = userRepository.findByUsername(principal.getName());

        // Lấy hồ sơ đất được gán cho user hiện tại với status = PROCESSING
        List<OpsDossier> dossiers = dossierRepository
                .findByCurrentHandlerIdAndDossierStatusAndServiceServiceCodeStartingWith(
                        currentUser.getId(), "PROCESSING", "DD");

        model.addAttribute("dossiers", dossiers);
        return "pages/land/appraisal-list";
    }

    /**
     * Trang chi tiết thẩm định
     * GET /land/appraisal/{id}
     */
    @GetMapping("/appraisal/{id}")
    public String showAppraisalDetail(@PathVariable Long id, Model model) {
        OpsDossier dossier = dossierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ"));

        model.addAttribute("dossier", dossier);

        return "pages/land/appraisal-detail";
    }

    /**
     * Xử lý thẩm định
     * POST /land/appraisal/submit
     */
    @PostMapping("/appraisal/submit")
    public String submitAppraisal(
            @RequestParam Long dossierId,
            @RequestParam String result, // PASS, FAIL
            @RequestParam String comments,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        try {
            SysUser currentUser = userRepository.findByUsername(principal.getName());

            DossierAppraisalDTO dto = new DossierAppraisalDTO();
            dto.setDossierId(dossierId);
            dto.setOfficerId(currentUser.getId());
            dto.setResult(result);
            dto.setComments(comments);

            landConversionService.appraiseDossier(dto);

            redirectAttributes.addFlashAttribute("message", "Thẩm định thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }

        return "redirect:/land/appraisal";
    }

    // ==================== BƯỚC 4: TRÌNH LÃNH ĐẠO ====================
    /**
     * Trang trình lãnh đạo
     * GET /land/submit-leader/{id}
     */
    @GetMapping("/submit-leader/{id}")
    public String showSubmitLeaderForm(@PathVariable Long id, Model model) {
        OpsDossier dossier = dossierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ"));

        // Lấy danh sách chủ tịch
        // TODO: Tạm thời lấy tất cả user, cần filter theo role LEADER sau
        List<SysUser> chairmen = userRepository.findAll().stream()
                .filter(u -> "OFFICIAL".equals(u.getUserType()))
                .toList();

        model.addAttribute("dossier", dossier);
        model.addAttribute("chairmen", chairmen);

        return "pages/land/submit-leader";
    }

    /**
     * Xử lý trình lãnh đạo
     * POST /land/submit-leader/submit
     */
    @PostMapping("/submit-leader/submit")
    public String submitToLeader(
            @RequestParam Long dossierId,
            @RequestParam Long chairmanId,
            @RequestParam String comments,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        try {
            SysUser currentUser = userRepository.findByUsername(principal.getName());

            landConversionService.submitToLeader(
                    dossierId, currentUser.getId(), chairmanId, comments);

            redirectAttributes.addFlashAttribute("message", "Trình lãnh đạo thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }

        return "redirect:/land/appraisal";
    }

    // ==================== BƯỚC 5: PHÊ DUYỆT ====================
    /**
     * Trang danh sách hồ sơ cần phê duyệt (cho Chủ tịch)
     * GET /land/approval
     */
    @GetMapping("/approval")
    public String showApprovalList(Model model, Principal principal) {
        SysUser currentUser = userRepository.findByUsername(principal.getName());

        // Lấy hồ sơ đất được gán cho chủ tịch với status = PROCESSING
        List<OpsDossier> dossiers = dossierRepository
                .findByCurrentHandlerIdAndDossierStatusAndServiceServiceCodeStartingWith(
                        currentUser.getId(), "PROCESSING", "DD");

        model.addAttribute("dossiers", dossiers);
        return "pages/land/approval-list";
    }

    /**
     * Trang chi tiết phê duyệt
     * GET /land/approval/{id}
     */
    @GetMapping("/approval/{id}")
    public String showApprovalDetail(@PathVariable Long id, Model model) {
        OpsDossier dossier = dossierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ"));

        // Lấy lịch sử xử lý
        List<OpsDossierLog> logs = logRepository.findByDossierIdOrderByCreatedAtAsc(id);

        model.addAttribute("dossier", dossier);
        model.addAttribute("logs", logs);

        return "pages/land/approval-detail";
    }

    /**
     * Xử lý phê duyệt
     * POST /land/approval/submit
     */
    @PostMapping("/approval/submit")
    public String submitApproval(
            @RequestParam Long dossierId,
            @RequestParam String decision, // APPROVE, REJECT
            @RequestParam String comments,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        try {
            SysUser currentUser = userRepository.findByUsername(principal.getName());

            DossierApprovalDTO dto = new DossierApprovalDTO();
            dto.setDossierId(dossierId);
            dto.setChairmanId(currentUser.getId());
            dto.setDecision(decision);
            dto.setComments(comments);

            landConversionService.chairmanApprove(dto);

            redirectAttributes.addFlashAttribute("message", "Phê duyệt thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }

        return "redirect:/land/approval";
    }

    // ==================== BƯỚC 6A: CẬP NHẬT SỔ ĐẤT ====================
    /**
     * Trang danh sách hồ sơ cần cập nhật sổ
     * GET /land/update-record
     */
    @GetMapping("/update-record")
    public String showUpdateRecordList(Model model, Principal principal) {
        // Lấy hồ sơ đã được phê duyệt, chưa cập nhật sổ
        // (Kiểm tra log xem đã có action APPROVE nhưng chưa có UPDATE_LAND_DB)
        List<OpsDossier> dossiers = dossierRepository
                .findByDossierStatusAndServiceServiceCodeStartingWith("PROCESSING", "DD");

        model.addAttribute("dossiers", dossiers);
        return "pages/land/update-record-list";
    }

    /**
     * Trang cập nhật sổ đất
     * GET /land/update-record/{id}
     */
    @GetMapping("/update-record/{id}")
    public String showUpdateRecordForm(@PathVariable Long id, Model model) {
        OpsDossier dossier = dossierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ"));

        model.addAttribute("dossier", dossier);

        return "pages/land/update-record";
    }

    /**
     * Xử lý cập nhật sổ
     * POST /land/update-record/submit
     */
    @PostMapping("/update-record/submit")
    public String submitUpdateRecord(
            @RequestParam Long dossierId,
            @RequestParam String comments,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        try {
            SysUser currentUser = userRepository.findByUsername(principal.getName());

            landConversionService.updateLandRecord(
                    dossierId, currentUser.getId(), comments);

            redirectAttributes.addFlashAttribute("message", "Cập nhật sổ đất thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }

        return "redirect:/land/update-record";
    }

    // ==================== BƯỚC 6B: HOÀN TẤT ====================
    /**
     * Trang danh sách hồ sơ cần hoàn tất
     * GET /land/finish
     */
    @GetMapping("/finish")
    public String showFinishList(Model model) {
        // Lấy hồ sơ đã cập nhật sổ, chưa hoàn tất
        List<OpsDossier> dossiers = dossierRepository
                .findByDossierStatusAndServiceServiceCodeStartingWith("PROCESSING", "DD");

        model.addAttribute("dossiers", dossiers);
        return "pages/land/finish-list";
    }

    /**
     * Xử lý hoàn tất hồ sơ
     * POST /land/finish/submit
     */
    @PostMapping("/finish/submit")
    public String submitFinish(
            @RequestParam Long dossierId,
            @RequestParam String comments,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        try {
            SysUser currentUser = userRepository.findByUsername(principal.getName());

            landConversionService.finishDossier(
                    dossierId, currentUser.getId(), comments);

            redirectAttributes.addFlashAttribute("message", "Hoàn tất hồ sơ thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }

        return "redirect:/land/finish";
    }

    // ==================== HELPER: XEM LỊCH SỬ ====================
    /**
     * Trang xem lịch sử xử lý hồ sơ
     * GET /land/history/{id}
     */
    @GetMapping("/history/{id}")
    public String showHistory(@PathVariable Long id, Model model) {
        OpsDossier dossier = dossierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ"));

        List<OpsDossierLog> logs = logRepository.findByDossierIdOrderByCreatedAtAsc(id);

        model.addAttribute("dossier", dossier);
        model.addAttribute("logs", logs);

        return "pages/land/history";
    }
}