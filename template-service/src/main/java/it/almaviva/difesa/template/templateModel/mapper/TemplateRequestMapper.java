package it.almaviva.difesa.template.templateModel.mapper;

import it.almaviva.difesa.shared.integration.mapper.GenericRequestMapper;
import it.almaviva.difesa.template.templateModel.dto.request.TemplateRequestCreateRequestDTO;
import it.almaviva.difesa.template.templateModel.entity.shared.Template;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;


@Mapper(componentModel = "spring")
public interface TemplateRequestMapper extends GenericRequestMapper<Template, TemplateRequestCreateRequestDTO> {

    @Mapping(source = "content", target = "htmlSource")
    @Mapping(source = "name", target = "name", qualifiedByName = "trimTemplateName")
    Template asEntity(TemplateRequestCreateRequestDTO dto);

    @Named("trimTemplateName")
    default String trimTemplateName(String templateName) {
        return templateName.trim();
    }
}
