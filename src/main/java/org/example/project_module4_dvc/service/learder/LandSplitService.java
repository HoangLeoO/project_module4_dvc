package org.example.project_module4_dvc.service.learder;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.project_module4_dvc.dto.formData.LandSplitMergeFormDTO;
import org.example.project_module4_dvc.entity.mock.MockLand;
import org.example.project_module4_dvc.entity.ops.OpsDossier;
import org.example.project_module4_dvc.repository.mock.MockLandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class LandSplitService {

    @Autowired
    private MockLandRepository mockLandRepository;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Xử lý tách thửa đất sau khi hồ sơ được phê duyệt
     * - Tìm thửa đất gốc
     * - Tạo các thửa mới
     * - Đánh dấu thửa gốc đã tách (land_status = 'SPLIT')
     */
    @Transactional
    public void splitLandFromDossier(OpsDossier dossier) {
        Map<String, Object> formData = dossier.getFormData();
        System.out.println("=== DEBUG: splitLandFromDossier called ===");
        System.out.println("DEBUG: Dossier ID = " + dossier.getId());
        System.out.println("DEBUG: FormData = " + formData);

        if (formData != null) {
            // Use ObjectMapper to convert Map to DTO
            LandSplitMergeFormDTO formDTO = objectMapper.convertValue(formData, LandSplitMergeFormDTO.class);

            String landCertNum = formDTO.getLandCertificateNumber();
            Integer numberOfNewPlots = formDTO.getNumberOfNewPlots();
            List<BigDecimal> splitAreas = formDTO.getRequestedSplitAreas();

            System.out.println("DEBUG: landCertificateNumber = " + landCertNum);
            System.out.println("DEBUG: numberOfNewPlots = " + numberOfNewPlots);
            System.out.println("DEBUG: requestedSplitAreas = " + splitAreas);

            if (landCertNum != null && numberOfNewPlots != null && splitAreas != null && !splitAreas.isEmpty()) {
                try {
                    System.out.println("DEBUG: Searching for MockLand with certificate number: " + landCertNum);

                    MockLand originalLand = mockLandRepository
                            .findByLandCertificateNumber(landCertNum)
                            .orElseThrow(() -> new RuntimeException(
                                    "Không tìm thấy sổ đỏ số: " + landCertNum));

                    System.out.println("DEBUG: Found original land ID = " + originalLand.getId());
                    System.out.println("DEBUG: Original status = " + originalLand.getLandStatus());

                    // VALIDATION 1: Check Status (Must be 'Hợp pháp')
                    if (!"Hợp pháp".equalsIgnoreCase(originalLand.getLandStatus())) {
                        throw new RuntimeException("Thửa đất không ở trạng thái 'Hợp pháp' (Hiện tại: "
                                + originalLand.getLandStatus() + "). Không thể tách thửa.");
                    }

                    // VALIDATION 2: Check Area Sum
                    BigDecimal totalSplitArea = splitAreas.stream()
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal originalArea = originalLand.getAreaM2();

                    // Allow small delta for floating point arithmetic (0.1 m2)
                    if (totalSplitArea.subtract(originalArea).abs().compareTo(new BigDecimal("0.1")) > 0) {
                        throw new RuntimeException("Tổng diện tích tách (" + totalSplitArea
                                + "m2) không khớp với diện tích gốc (" + originalArea + "m2)");
                    }

                    // VALIDATION 3: Minimum Area (e.g. 30m2)
                    for (BigDecimal area : splitAreas) {
                        if (area.compareTo(new BigDecimal(30)) < 0) {
                            throw new RuntimeException(
                                    "Diện tích thửa tách (" + area + "m2) nhỏ hơn mức tối thiểu (30m2)");
                        }
                    }

                    // Tạo các thửa mới
                    for (int i = 0; i < splitAreas.size(); i++) {
                        BigDecimal newArea = splitAreas.get(i);

                        MockLand newPlot = new MockLand();
                        // Copy thông tin từ thửa gốc
                        newPlot.setOwner(originalLand.getOwner());
                        newPlot.setAddressDetail(originalLand.getAddressDetail());
                        newPlot.setLandPurpose(originalLand.getLandPurpose());
                        newPlot.setUsageForm(originalLand.getUsageForm());
                        newPlot.setUsagePeriod(originalLand.getUsagePeriod());
                        newPlot.setIssueAuthority(originalLand.getIssueAuthority());
                        newPlot.setIssueDate(originalLand.getIssueDate());
                        newPlot.setMapSheetNumber(originalLand.getMapSheetNumber());

                        // Thông tin mới
                        newPlot.setAreaM2(newArea);
                        newPlot.setParcelNumber(originalLand.getParcelNumber() + "-" + (char) ('A' + i));
                        newPlot.setLandCertificateNumber(originalLand.getLandCertificateNumber() + "-" + (i + 1));
                        newPlot.setLandStatus("Hợp pháp"); // Consist with SQL data

                        mockLandRepository.save(newPlot);
                        System.out.println("DEBUG: Created new plot " + (i + 1) + ": " + newPlot.getParcelNumber()
                                + " with area " + newArea + " m²");
                    }

                    // Xóa thửa đất cũ
                    mockLandRepository.delete(originalLand);

                    System.out.println("DEBUG: Deleted original land");
                    System.out.println("DEBUG: Land split completed successfully!");

                } catch (Exception e) {
                    System.err.println("ERROR: Failed to split land - " + e.getMessage());
                    throw e; // Rethrow to propagate error
                }
            } else {
                System.out.println("DEBUG: Missing required data for land split");
                throw new RuntimeException("Dữ liệu tách thửa không đầy đủ (Sổ đỏ, Số thửa mới, Diện tích tách)");
            }
        } else {
            System.out.println("DEBUG: FormData is null!");
        }
        System.out.println("=== DEBUG: splitLandFromDossier completed ===");
    }
}
