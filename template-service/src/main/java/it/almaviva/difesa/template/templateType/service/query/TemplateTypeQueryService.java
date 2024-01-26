package it.almaviva.difesa.template.templateType.service.query;


import it.almaviva.difesa.template.shared.criteria.TemplateTypeCriteria;
import it.almaviva.difesa.template.templateType.dto.response.TemplateTypeFilterResponseSearchDTO;
import java.util.List;


public interface TemplateTypeQueryService {

    List<TemplateTypeFilterResponseSearchDTO> filterTemplateTypes(TemplateTypeCriteria templateTypeCriteria);
}
