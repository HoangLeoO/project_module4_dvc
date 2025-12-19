package org.example.project_module4_dvc.repository.cat;

import org.example.project_module4_dvc.entity.cat.CatService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CatServiceRepository extends JpaRepository<CatService, Long> {
    Optional<CatService> findByServiceCode(String serviceCode);

    List<CatService> findByServiceNameContainingIgnoreCaseOrDomainContainingIgnoreCase(String name, String domain);

    List<CatService> findByServiceNameContainingIgnoreCaseAndDomainContainingIgnoreCase(String name, String domain);

    List<CatService> findByDomainContainingIgnoreCase(String domain);
}