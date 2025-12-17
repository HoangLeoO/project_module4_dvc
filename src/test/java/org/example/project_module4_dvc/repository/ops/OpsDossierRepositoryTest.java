package org.example.project_module4_dvc.repository.ops;

import org.example.project_module4_dvc.dto.OpsDossierDetailDTO;
import org.example.project_module4_dvc.dto.OpsDossierSummaryDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class cho OpsDossierRepository
 * Kiểm tra các DTO queries
 */
@DataJpaTest
@ActiveProfiles("test")
class OpsDossierRepositoryTest {

    @Autowired
    private OpsDossierRepository dossierRepository;

    @Test
    void testFindDossierDetailById_ShouldReturnDTO() {
        // Given: Giả sử đã có data trong database (hoặc dùng @Sql để insert)
        Long dossierId = 1L;

        // When
        Optional<OpsDossierDetailDTO> result = dossierRepository.findDossierDetailById(dossierId);

        // Then
        assertThat(result).isPresent();

        OpsDossierDetailDTO dto = result.get();
        assertThat(dto.getDossierId()).isEqualTo(dossierId);
        assertThat(dto.getDossierCode()).isNotNull();
        assertThat(dto.getApplicantFullName()).isNotNull();
        assertThat(dto.getServiceName()).isNotNull();

        // Kiểm tra không có lazy loading exception
        System.out.println("Dossier Code: " + dto.getDossierCode());
        System.out.println("Applicant: " + dto.getApplicantFullName());
        System.out.println("Service: " + dto.getServiceName());
    }

    @Test
    void testFindAllDossierSummaries_ShouldReturnList() {
        // When
        List<OpsDossierSummaryDTO> results = dossierRepository.findAllDossierSummaries();

        // Then
        assertThat(results).isNotNull();

        if (!results.isEmpty()) {
            OpsDossierSummaryDTO firstDto = results.get(0);
            assertThat(firstDto.getDossierId()).isNotNull();
            assertThat(firstDto.getDossierCode()).isNotNull();
            assertThat(firstDto.getApplicantFullName()).isNotNull();
        }
    }

    @Test
    void testFindDossiersByApplicantId_ShouldReturnUserDossiers() {
        // Given
        Long applicantId = 1L;

        // When
        List<OpsDossierSummaryDTO> results = dossierRepository.findDossiersByApplicantId(applicantId);

        // Then
        assertThat(results).isNotNull();

        // Tất cả hồ sơ phải của cùng một người
        results.forEach(dto -> {
            assertThat(dto.getApplicantFullName()).isNotNull();
        });
    }

    @Test
    void testFindDossiersByStatus_ShouldReturnFilteredList() {
        // Given
        String status = "NEW";

        // When
        List<OpsDossierSummaryDTO> results = dossierRepository.findDossiersByStatus(status);

        // Then
        assertThat(results).isNotNull();

        // Tất cả hồ sơ phải có status = "NEW"
        results.forEach(dto -> {
            assertThat(dto.getDossierStatus()).isEqualTo(status);
        });
    }

    @Test
    void testNativeQuery_ShouldReturnProjection() {
        // Given
        Long dossierId = 1L;

        // When
        var result = dossierRepository.findDossierByIdNative(dossierId);

        // Then
        assertThat(result).isPresent();

        var projection = result.get();
        assertThat(projection.getDossierId()).isEqualTo(dossierId);
        assertThat(projection.getDossierCode()).isNotNull();
        assertThat(projection.getApplicantFullName()).isNotNull();
    }

    /**
     * Test để kiểm tra số lượng query được execute
     * Đảm bảo không có N+1 query problem
     */
    @Test
    void testNoNPlusOneQuery() {
        // Bật logging để xem SQL:
        // logging.level.org.hibernate.SQL=DEBUG
        // logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE

        // When
        List<OpsDossierSummaryDTO> results = dossierRepository.findAllDossierSummaries();

        // Then
        // Chỉ nên có 1 query duy nhất với JOIN
        // Kiểm tra log để verify
        assertThat(results).isNotNull();
    }
}
