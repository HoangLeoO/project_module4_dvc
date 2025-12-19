package org.example.project_module4_dvc.repository.mod;

import org.example.project_module4_dvc.entity.mod.ModPersonalVault;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModPersonalVaultRepository extends JpaRepository<ModPersonalVault, Long> {
    List<ModPersonalVault> findByUserId(Long userId);
}