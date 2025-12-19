package org.example.project_module4_dvc.mapper;

import org.example.project_module4_dvc.dto.dossier.NewDossierDTO;
import org.example.project_module4_dvc.entity.ops.OpsDossier;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OpsDossierMapper {
    @Mapping(source = "applicant.fullName", target = "applicantFullName")
    @Mapping(source = "service.serviceName", target = "serviceName")
    @Mapping(source = "applicant.citizen.cccd", target = "cccd")
    NewDossierDTO toDTO(OpsDossier opsDossier);

}
