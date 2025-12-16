package org.example.project_module4_dvc.repository.mod;

import org.example.project_module4_dvc.entity.mod.ModNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModNotificationRepository extends JpaRepository<ModNotification, Long> {
}