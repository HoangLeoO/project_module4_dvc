package org.example.project_module4_dvc.service.mock;

import org.springframework.stereotype.Service;

@Service
public class MockCitizenService implements IMockCitizenService {

    @org.springframework.beans.factory.annotation.Autowired
    private org.example.project_module4_dvc.repository.mock.MockCitizenRepository citizenRepository;

    @Override
    public org.example.project_module4_dvc.entity.mock.MockCitizen findById(Long id) {
        return citizenRepository.findById(id).orElse(null);
    }

    @Override
    public org.example.project_module4_dvc.entity.mock.MockCitizen findSpouseByCitizenId(Long citizenId) {
        org.example.project_module4_dvc.entity.mock.MockCitizen citizen = findById(citizenId);
        if (citizen == null || citizen.getSpouseId() == null)
            return null;

        return findById(citizen.getSpouseId());
    }
}
