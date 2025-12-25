package org.example.project_module4_dvc.controller.citizen;

import jakarta.servlet.http.HttpServletRequest;
import org.example.project_module4_dvc.entity.ops.OpsDossier;
import org.example.project_module4_dvc.repository.ops.OpsDossierRepository;
import org.example.project_module4_dvc.service.payment.PaymentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Controller
public class PaymentController {

    private final PaymentService paymentService;
    private final OpsDossierRepository opsDossierRepository;

    public PaymentController(PaymentService paymentService, OpsDossierRepository opsDossierRepository) {
        this.paymentService = paymentService;
        this.opsDossierRepository = opsDossierRepository;
    }

    @GetMapping("/payment/vnpay-return")
    public String vnpayReturn(HttpServletRequest request, Model model) {
        Map<String, String> vnp_Params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (Map.Entry<String, String[]> entry : requestParams.entrySet()) {
            if (entry.getValue().length > 0) {
                vnp_Params.put(entry.getKey(), entry.getValue()[0]);
            }
        }

        String transactionCode = vnp_Params.get("vnp_TransactionNo");
        String dossierCode = vnp_Params.get("vnp_TxnRef");
        String responseCode = vnp_Params.get("vnp_ResponseCode");
        String secureHash = vnp_Params.get("vnp_SecureHash");

        // Validate signature
        // Note: In a real scenario, you should strictly validate using
        // paymentService.validateCallback(vnp_Params)
        // However, for simplicity and manual testing without public IP, we might assume
        // validity or inspect carefully.
        boolean isValid = paymentService.validateCallback(vnp_Params);

        if (isValid) {
            OpsDossier dossier = opsDossierRepository.findByDossierCode(dossierCode)
                    .orElse(null);

            if (dossier != null) {
                if ("00".equals(responseCode)) {
                    // Success
                    dossier.setPaymentStatus("PAID");
                    // Change dossier status from PENDING_PAYMENT to NEW so officers can see it
                    dossier.setDossierStatus("NEW");
                    dossier.setPaymentDate(LocalDateTime.now());
                    dossier.setTransactionCode(transactionCode);
                    dossier.setPaymentAmount(Long.parseLong(vnp_Params.get("vnp_Amount")) / 100);
                    model.addAttribute("message", "Thanh toán thành công!");
                    model.addAttribute("status", "success");
                } else {
                    // Failed
                    dossier.setPaymentStatus("FAILED");
                    model.addAttribute("message", "Thanh toán thất bại. Mã lỗi: " + responseCode);
                    model.addAttribute("status", "error");
                }
                opsDossierRepository.save(dossier);
                model.addAttribute("dossierCode", dossierCode);
            } else {
                model.addAttribute("message", "Không tìm thấy hồ sơ tương ứng.");
                model.addAttribute("status", "error");
            }
        } else {
            model.addAttribute("message", "Chữ ký không hợp lệ!");
            model.addAttribute("status", "error");
        }

        return "pages/portal/payment/payment-result";
    }
}
