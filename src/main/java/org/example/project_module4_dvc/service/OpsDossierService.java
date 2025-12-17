package org.example.project_module4_dvc.service;

import lombok.RequiredArgsConstructor;
import org.example.project_module4_dvc.dto.OpsDossierDetailDTO;
import org.example.project_module4_dvc.dto.OpsDossierSummaryDTO;
import org.example.project_module4_dvc.repository.ops.OpsDossierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service xử lý nghiệp vụ liên quan đến hồ sơ
 * Sử dụng DTO để hiển thị thông tin từ nhiều bảng
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // Mặc định là read-only cho performance
public class OpsDossierService {

    private final OpsDossierRepository dossierRepository;

    /**
     * Lấy thông tin chi tiết hồ sơ (từ nhiều bảng)
     * 
     * @param dossierId ID của hồ sơ
     * @return OpsDossierDetailDTO chứa thông tin từ 4 bảng
     * @throws RuntimeException nếu không tìm thấy hồ sơ
     */
    public OpsDossierDetailDTO getDossierDetail(Long dossierId) {
        return dossierRepository.findDossierDetailById(dossierId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ với ID: " + dossierId));
    }

    /**
     * Lấy danh sách tất cả hồ sơ (tóm tắt)
     * 
     * @return Danh sách OpsDossierSummaryDTO
     */
    public List<OpsDossierSummaryDTO> getAllDossiers() {
        return dossierRepository.findAllDossierSummaries();
    }

    /**
     * Lấy danh sách hồ sơ của một người nộp
     * 
     * @param applicantId ID của người nộp hồ sơ
     * @return Danh sách hồ sơ của người đó
     */
    public List<OpsDossierSummaryDTO> getDossiersByApplicant(Long applicantId) {
        return dossierRepository.findDossiersByApplicantId(applicantId);
    }

    /**
     * Lấy danh sách hồ sơ được phân công cho một cán bộ
     * 
     * @param handlerId ID của cán bộ thụ lý
     * @return Danh sách hồ sơ được phân công
     */
    public List<OpsDossierSummaryDTO> getDossiersByHandler(Long handlerId) {
        return dossierRepository.findDossiersByHandlerId(handlerId);
    }

    /**
     * Lấy danh sách hồ sơ theo trạng thái
     * 
     * @param status Trạng thái hồ sơ (NEW, PROCESSING, APPROVED, REJECTED, ...)
     * @return Danh sách hồ sơ có trạng thái tương ứng
     */
    public List<OpsDossierSummaryDTO> getDossiersByStatus(String status) {
        return dossierRepository.findDossiersByStatus(status);
    }
}
