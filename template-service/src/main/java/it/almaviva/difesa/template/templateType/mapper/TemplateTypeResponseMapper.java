package it.almaviva.difesa.template.templateType.mapper;


import org.mapstruct.Mapper;

import it.almaviva.difesa.shared.integration.mapper.GenericResponseMapper;
import it.almaviva.difesa.template.templateType.dto.response.TemplateTypeFilterResponseSearchDTO;
import it.almaviva.difesa.template.templateType.entity.shared.TemplateType;


@Mapper(componentModel = "spring")
public interface TemplateTypeResponseMapper extends GenericResponseMapper<TemplateType, TemplateTypeFilterResponseSearchDTO> {


}
