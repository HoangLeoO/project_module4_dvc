package org.example.project_module4_dvc.service.autoFill;

import org.example.project_module4_dvc.dto.RelationshipResult;
import org.example.project_module4_dvc.entity.mock.MockCitizen;
import org.example.project_module4_dvc.entity.mock.MockHouseholdMember;
import org.example.project_module4_dvc.entity.mock.MockLand;
import org.example.project_module4_dvc.repository.autoFill.AutoFillCitizenRepository;
import org.example.project_module4_dvc.repository.autoFill.AutoFillLandRepository;
import org.example.project_module4_dvc.repository.mock.MockHouseholdMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AutoFillService {
    @Autowired
    private AutoFillCitizenRepository autoFillCitizenRepository;

    @Autowired
    private AutoFillLandRepository autoFillLandRepository;

    @Autowired
    private MockHouseholdMemberRepository mockHouseholdMemberRepository;

    public MockCitizen getCitizenByCccd(String cccd) {
        return autoFillCitizenRepository.findByCccd(cccd).orElse(null);
    }
    
    public MockCitizen getCitizenById(Long id) {
        return autoFillCitizenRepository.findById(id).orElse(null);
    }

    public List<MockLand> getLandsByOwnerCccd(String cccd) {
        return autoFillLandRepository.findAllByOwner_Cccd(cccd);
    }

    public RelationshipResult checkHouseholdRelationship(Long userCitizenId, String targetCccd) {
        // 1. Find household of current user
        List<MockHouseholdMember> userMembers = mockHouseholdMemberRepository.findByCitizen_IdAndStatus(userCitizenId, 1);
        if (userMembers.isEmpty()) {
            return new RelationshipResult(false, "Người dùng không thuộc hộ khẩu nào.");
        }

        // 2. Find household of target citizen
        List<MockHouseholdMember> targetMembers = mockHouseholdMemberRepository.findByCitizen_CccdAndStatus(targetCccd, 1);
        if (targetMembers.isEmpty()) {
            return new RelationshipResult(false, "Công dân được ủy quyền không thuộc hộ khẩu nào hoặc không tồn tại.");
        }

        // 3. Check for common household
        for (MockHouseholdMember uMember : userMembers) {
            for (MockHouseholdMember tMember : targetMembers) {
                if (uMember.getHousehold().getId().equals(tMember.getHousehold().getId())) {
                    // Match found!
                    String relation = "Cùng hộ khẩu (" + uMember.getHousehold().getHouseholdCode() + ")";
                    String detail = "Quan hệ: " + uMember.getRelationToHead() + " - " + tMember.getRelationToHead();
                    return new RelationshipResult(true, relation, detail, tMember.getCitizen().getFullName());
                }
            }
        }

        return new RelationshipResult(false, "Không tìm thấy mối quan hệ hộ khẩu chung.");
    }
}
