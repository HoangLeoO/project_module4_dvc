package org.example.project_module4_dvc.service.sys;

import lombok.RequiredArgsConstructor;
import org.example.project_module4_dvc.entity.sys.SysDepartment;
import org.example.project_module4_dvc.repository.sys.SysDepartmentRepository;
import org.example.project_module4_dvc.service.iml.ISysDepartmentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SysDepartmentService implements ISysDepartmentService {
    private final SysDepartmentRepository sysDepartmentRepository;
    @Override
    public SysDepartment getDept() {
        return new SysDepartment();
    }

    @Override
    public List<SysDepartment> getAll() {
        return sysDepartmentRepository.findAll();
    }
}
