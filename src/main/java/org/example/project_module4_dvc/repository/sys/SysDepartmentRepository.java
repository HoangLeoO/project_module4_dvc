package org.example.project_module4_dvc.repository.sys;

import org.example.project_module4_dvc.entity.sys.SysDepartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SysDepartmentRepository extends JpaRepository<SysDepartment, Long> {
    List<SysDepartment> getSysDepartmentById(Long id);
    SysDepartment findByDeptCode(String deptCode);
    List<SysDepartment> findAllByLevel(Integer level);
}