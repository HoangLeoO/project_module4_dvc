package org.example.project_module4_dvc.service.mock;

import org.example.project_module4_dvc.entity.mock.MockCitizen;

public interface IMockCitizenService {
    MockCitizen findById(Long id);

    MockCitizen findSpouseByCitizenId(Long citizenId);
}
