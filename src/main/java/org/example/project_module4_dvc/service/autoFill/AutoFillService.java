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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(readOnly = true)
@Slf4j
public class AutoFillService {
    @Autowired
    private AutoFillCitizenRepository autoFillCitizenRepository;

    @Autowired
    private AutoFillLandRepository autoFillLandRepository;

    @Autowired
    private MockHouseholdMemberRepository mockHouseholdMemberRepository;

    @Autowired
    private org.example.project_module4_dvc.repository.mock.MockCitizenRelationshipRepository mockCitizenRelationshipRepository;

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
        log.info("Checking relationship between UserID={} and TargetCCCD={}", userCitizenId, targetCccd);

        // 0. Get Target Citizen ID
        Optional<MockCitizen> targetOpt = autoFillCitizenRepository.findByCccd(targetCccd);
        if (targetOpt.isEmpty()) {
             log.warn("Target citizen not found for CCCD={}", targetCccd);
             return new RelationshipResult(false, "Không tìm thấy công dân với CCCD đã nhập.");
        }
        Long targetId = targetOpt.get().getId();
        log.info("Target Citizen ID found: {}", targetId);

        // 1. Find household of current user
        List<MockHouseholdMember> userMembers = mockHouseholdMemberRepository.findByCitizen_IdAndStatus(userCitizenId, 1);
        log.info("User {} belongs to {} active households", userCitizenId, userMembers.size());
        
        // 2. Find household of target citizen
        List<MockHouseholdMember> targetMembers = mockHouseholdMemberRepository.findByCitizen_CccdAndStatus(targetCccd, 1);
        log.info("Target {} (CCCD {}) belongs to {} active households", targetId, targetCccd, targetMembers.size());
        
        // 3. Check for common household
        if (!userMembers.isEmpty() && !targetMembers.isEmpty()) {
            for (MockHouseholdMember uMember : userMembers) {
                for (MockHouseholdMember tMember : targetMembers) {
                    log.debug("Comparing UserHousehold={} with TargetHousehold={}", uMember.getHousehold().getId(), tMember.getHousehold().getId());
                    if (uMember.getHousehold().getId().equals(tMember.getHousehold().getId())) {
                        String relation = "Cùng hộ khẩu (" + uMember.getHousehold().getHouseholdCode() + ")";
                        String detail = "Quan hệ: " + uMember.getRelationToHead() + " - " + tMember.getRelationToHead();
                        log.info("Household match found: {}", relation);
                        return new RelationshipResult(true, relation, detail, tMember.getCitizen().getFullName());
                    }
                }
            }
        }

        // 4. Check 3 Generations (BFS)
        log.info("No household match. Starting BFS check for 3 generations...");
        RelationshipPath path = findRelationshipPath(userCitizenId, targetId);
        if (path != null) {
             log.info("BFS Relationship found: {}", path.description);
             return new RelationshipResult(true, "Quan hệ gia đình (Trong phạm vi 3 đời)", path.description, targetOpt.get().getFullName());
        }

        log.info("No relationship found.");
        StringBuilder debugMsg = new StringBuilder();
        debugMsg.append("Không tìm thấy mối quan hệ trong hộ khẩu hoặc trong phạm vi 3 đời.");
        debugMsg.append(String.format(" [DEBUG: UserID=%d, TargetID=%d. ", userCitizenId, targetId));
        
        // Debug neighbors
        List<org.example.project_module4_dvc.entity.mock.MockCitizenRelationship> forward = mockCitizenRelationshipRepository.findByCitizenId(userCitizenId);
        debugMsg.append("Neighbors(Fwd): ").append(forward.size());
        if (!forward.isEmpty()) {
             debugMsg.append(" -> {");
             for(var r : forward) debugMsg.append(r.getRelative().getId()).append(",");
             debugMsg.append("}");
        }
        
        List<org.example.project_module4_dvc.entity.mock.MockCitizenRelationship> backward = mockCitizenRelationshipRepository.findByRelativeId(userCitizenId);
        debugMsg.append(". Neighbors(Bwd): ").append(backward.size());
        if (!backward.isEmpty()) {
             debugMsg.append(" -> {");
             for(var r : backward) debugMsg.append(r.getCitizen().getId()).append(",");
             debugMsg.append("}");
        }
        debugMsg.append("]");

        return new RelationshipResult(false, debugMsg.toString());
    }

    // --- Helper for Graph Search ---
    private RelationshipPath findRelationshipPath(Long startId, Long endId) {
        if (startId.equals(endId)) return new RelationshipPath("Bản thân");

        java.util.Queue<BFSNode> queue = new java.util.LinkedList<>();
        java.util.Set<Long> visited = new java.util.HashSet<>();
        
        queue.add(new BFSNode(startId, "", 0));
        visited.add(startId);

        while (!queue.isEmpty()) {
            BFSNode current = queue.poll();
            
            if (current.depth >= 3) continue; // Max depth 3

            // Get Neighbors
            // 1. From relationships table as Source
            List<org.example.project_module4_dvc.entity.mock.MockCitizenRelationship> forwardRels = mockCitizenRelationshipRepository.findByCitizenId(current.id);
            for (org.example.project_module4_dvc.entity.mock.MockCitizenRelationship rel : forwardRels) {
                // Check if rel.getRelative() is null or lazy loaded issues?
                if (rel.getRelative() == null) continue;
                
                Long neighborId = rel.getRelative().getId();
                // log.debug("BFS Forward: {} -> {} ({})", current.id, neighborId, rel.getRelationshipType());
                
                if (neighborId.equals(endId)) {
                    return new RelationshipPath(current.path + (current.path.isEmpty() ? "" : " -> ") + rel.getRelationshipType());
                }
                if (!visited.contains(neighborId)) {
                    visited.add(neighborId);
                    queue.add(new BFSNode(neighborId, current.path + (current.path.isEmpty() ? "" : " -> ") + rel.getRelationshipType(), current.depth + 1));
                }
            }

            // 2. From relationships table as Target (Reverse)
            List<org.example.project_module4_dvc.entity.mock.MockCitizenRelationship> backwardRels = mockCitizenRelationshipRepository.findByRelativeId(current.id);
            for (org.example.project_module4_dvc.entity.mock.MockCitizenRelationship rel : backwardRels) {
                 if (rel.getCitizen() == null) continue;

                 Long neighborId = rel.getCitizen().getId();
                 // Infer reverse relationship name roughly
                 String reverseType = "Người nhà của " + rel.getRelationshipType(); 
                 // log.debug("BFS Backward: {} <- {} ({})", current.id, neighborId, rel.getRelationshipType());

                 if (neighborId.equals(endId)) {
                     return new RelationshipPath(current.path + (current.path.isEmpty() ? "" : " -> ") + reverseType);
                 }
                 if (!visited.contains(neighborId)) {
                     visited.add(neighborId);
                     queue.add(new BFSNode(neighborId, current.path + (current.path.isEmpty() ? "" : " -> ") + reverseType, current.depth + 1));
                 }
            }
        }
        return null;
    }

    private static class BFSNode {
        Long id;
        String path;
        int depth;

        public BFSNode(Long id, String path, int depth) {
            this.id = id;
            this.path = path;
            this.depth = depth;
        }
    }

    private static class RelationshipPath {
        String description;
        public RelationshipPath(String description) { this.description = description; }
    }
}
