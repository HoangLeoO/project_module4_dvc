package org.example.project_module4_dvc.controller.api;

import org.example.project_module4_dvc.repository.mock.MockCitizenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/citizen")
public class CitizenLookupController {

    @Autowired
    private MockCitizenRepository mockCitizenRepository;

    @GetMapping("/lookup/{cccd}")
    public ResponseEntity<?> lookupCitizen(@PathVariable String cccd) {
        return mockCitizenRepository.findByCccd(cccd)
                .map(citizen -> {
                    String dobStr = citizen.getDob() != null ? citizen.getDob().toString() : "";
                    String genderStr = citizen.getGender() != null ? citizen.getGender() : "";
                    String maritalStr = citizen.getMaritalStatus() != null ? citizen.getMaritalStatus() : "";

                    return ResponseEntity.ok(java.util.Map.of(
                            "fullName", citizen.getFullName(),
                            "dob", dobStr,
                            "gender", genderStr,
                            "maritalStatus", maritalStr,
                            "address", citizen.getPermanentAddress() != null ? citizen.getPermanentAddress() : ""));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
