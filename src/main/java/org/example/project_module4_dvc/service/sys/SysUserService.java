package org.example.project_module4_dvc.service.sys;

import org.example.project_module4_dvc.dto.admin.AdminUserListDTO;
import org.example.project_module4_dvc.entity.sys.SysUser;
import org.example.project_module4_dvc.repository.sys.SysUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SysUserService implements ISysUserService {

    @Autowired
    private SysUserRepository userRepository;

    @Override
    public SysUser findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public SysUser findById(Long userId) {
        return  userRepository.findById(userId).orElse(null);
    }

    @Override
    public List<AdminUserListDTO> getOfficials() {

        List<Object[]> rows = userRepository.findOfficialsRaw();

        Map<Long, AdminUserListDTO> map = new LinkedHashMap<>();

        for (Object[] r : rows) {
            Long id = (Long) r[0];

            map.computeIfAbsent(id, k ->
                    new AdminUserListDTO(
                            id,
                            (String) r[1],
                            (String) r[2],
                            (String) r[3],
                            new ArrayList<>()
                    )
            );

            if (r[4] != null) {
                map.get(id).getRoles().add((String) r[4]);
            }
        }

        return new ArrayList<>(map.values());
    }
}
