package org.example.project_module4_dvc.mapper;

import org.example.project_module4_dvc.dto.dossier.ResultDossierDTO;
import org.example.project_module4_dvc.entity.ops.OpsDossierResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OpsDossierResultMapper {
    @Mapping(source = "dossier.dossierCode", target = "dossierCode")
    @Mapping(source = "dossier.dossierStatus", target = "dossierStatus")
    @Mapping(source = "dossier.submissionDate", target = "submissionDate")
    @Mapping(source = "dossier.dueDate", target = "dueDate")
    @Mapping(source = "dossier.finishDate", target = "finishDate")
    @Mapping(source = "dossier.applicant.fullName", target = "applicantFullName")
    @Mapping(source = "dossier.service.serviceName", target = "serviceName")
    @Mapping(source = "dossier.applicant.citizen.cccd", target = "cccd")
    ResultDossierDTO toDTO(OpsDossierResult opsDossierResult);
}
