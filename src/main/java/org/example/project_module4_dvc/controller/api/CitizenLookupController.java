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
                .map(citizen -> ResponseEntity.ok(java.util.Map.of(
                        "fullName", citizen.getFullName(),
                        "dob", citizen.getDob(),
                        "gender", citizen.getGender(),
                        "maritalStatus", citizen.getMaritalStatus(),
                        "address", citizen.getPermanentAddress())))
                .orElse(ResponseEntity.notFound().build());
    }
}
