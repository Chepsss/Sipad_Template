package it.almaviva.difesa.template.templateModel.mapper;


import it.almaviva.difesa.shared.integration.mapper.GenericResponseMapper;
import it.almaviva.difesa.template.templateModel.dto.response.TemplateFilterResponseSearchDTO;
import it.almaviva.difesa.template.templateModel.entity.shared.Template;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = TemplateResponseMapper.class)
public interface TemplateFilterResponseMapper extends GenericResponseMapper<Template, TemplateFilterResponseSearchDTO> {

    @Mapping(source = "htmlSource", target = "content")
    @Mapping(source = "validityEndDate", target = "validityEndDate", qualifiedByName = "validityEndDate")
    TemplateFilterResponseSearchDTO asDTO(Template template);
}
