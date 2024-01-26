package it.almaviva.difesa.template.templateModel.mapper;

import it.almaviva.difesa.shared.integration.mapper.GenericResponseMapper;
import it.almaviva.difesa.template.templateModel.dto.response.TemplateResponseDTO;
import it.almaviva.difesa.template.templateModel.entity.shared.Template;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDate;

@Mapper(componentModel = "spring")
public interface TemplateResponseMapper extends GenericResponseMapper<Template, TemplateResponseDTO> {

    @Mapping(source = "htmlSource", target = "content")
    @Mapping(source = "validityEndDate", target = "validityEndDate", qualifiedByName = "validityEndDate")
    TemplateResponseDTO asDTO(Template entity);

    @Named("validityEndDate")
    default LocalDate getValidityEndDate(LocalDate validityEndDate) {
        if (validityEndDate.isEqual(LocalDate.of(9999, 12, 31))) {
            return null;
        }
        return validityEndDate;
    }
}
