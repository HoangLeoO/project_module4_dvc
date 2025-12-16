package org.example.project_module4_dvc.repository.sys;

import org.example.project_module4_dvc.entity.sys.SysConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SysConfigRepository extends JpaRepository<SysConfig, String> {
}