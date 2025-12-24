package org.example.project_module4_dvc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.project_module4_dvc.entity.mock.MockCitizen;

/**
 * DTO để hiển thị thông tin người thân trong gia đình
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FamilyMemberDTO {

    /**
     * Thông tin công dân (người thân)
     */
    private MockCitizen citizen;

    /**
     * Mối quan hệ với người dùng hiện tại
     * VD: CHA, ME, CON, VO, CHONG, ANH, CHI, EM
     */
    private String relationshipType;

    /**
     * Tên hiển thị của mối quan hệ (để dễ đọc)
     */
    private String relationshipDisplayName;

    /**
     * Chuyển đổi mã quan hệ sang tên hiển thị
     */
    public String getRelationshipDisplayName() {
        if (relationshipDisplayName != null) {
            return relationshipDisplayName;
        }

        // Tự động chuyển đổi nếu chưa set
        return switch (relationshipType != null ? relationshipType.toUpperCase() : "") {
            case "CHA" -> "Cha";
            case "ME" -> "Mẹ";
            case "CON" -> "Con";
            case "VO" -> "Vợ";
            case "CHONG" -> "Chồng";
            case "ANH" -> "Anh";
            case "CHI" -> "Chị";
            case "EM" -> "Em";
            case "ONG" -> "Ông";
            case "BA" -> "Bà";
            case "CHAU" -> "Cháu";
            default -> relationshipType;
        };
    }
}
