package org.example.project_module4_dvc.repository.sys;

import org.example.project_module4_dvc.entity.sys.SysDepartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SysDepartmentRepository extends JpaRepository<SysDepartment, Long> {
}