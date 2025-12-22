package org.example.project_module4_dvc.service.iml;

import org.example.project_module4_dvc.entity.sys.SysDepartment;

import java.util.List;

public interface ISysDepartmentService {
    SysDepartment getDept();

    List<SysDepartment> getAll();
}
