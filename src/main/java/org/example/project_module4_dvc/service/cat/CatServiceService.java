package org.example.project_module4_dvc.service.cat;

import lombok.RequiredArgsConstructor;
import org.example.project_module4_dvc.dto.cat.CatServiceDTO;
import org.example.project_module4_dvc.entity.cat.CatService;
import org.example.project_module4_dvc.repository.cat.CatServiceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CatServiceService implements ICatServiceService {

    private final CatServiceRepository catServiceRepository;

    @Override
    public List<CatServiceDTO> getAllServices() {
        return catServiceRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CatServiceDTO> getFeaturedServices() {
        // For now, just return the first 4 services as "featured"
        // In a real app, this might be based on usage stats or a "featured" flag
        return catServiceRepository.findAll().stream()
                .limit(4)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CatServiceDTO getServiceByCode(String code) {
        return catServiceRepository.findByServiceCode(code)
                .map(this::convertToDTO)
                .orElse(null);
    }

    @Override
    public List<CatServiceDTO> searchServices(String keyword, String domain) {
        List<CatService> results;
        boolean hasKeyword = keyword != null && !keyword.isEmpty();
        boolean hasDomain = domain != null && !domain.isEmpty() && !"ALL".equals(domain);

        if (hasKeyword && hasDomain) {
            results = catServiceRepository.findByServiceNameContainingIgnoreCaseAndDomainContainingIgnoreCase(keyword,
                    domain);
        } else if (hasKeyword) {
            results = catServiceRepository.findByServiceNameContainingIgnoreCaseOrDomainContainingIgnoreCase(keyword,
                    keyword);
        } else if (hasDomain) {
            results = catServiceRepository.findByDomainContainingIgnoreCase(domain);
        } else {
            return getAllServices();
        }

        return results.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private CatServiceDTO convertToDTO(CatService entity) {
        return CatServiceDTO.builder()
                .id(entity.getId())
                .serviceCode(entity.getServiceCode())
                .serviceName(entity.getServiceName())
                .domain(entity.getDomain())
                .slaHours(entity.getSlaHours())
                .feeAmount(entity.getFeeAmount())
                .build();
    }

    @Override
    public List<CatService> findAll() {
        return catServiceRepository.findAll();
    }
}
