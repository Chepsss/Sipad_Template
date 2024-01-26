package it.almaviva.difesa.template.shared.criteria;

import it.almaviva.difesa.shared.data.criteria.GenericCriteriaModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TemplateTypeCriteria implements GenericCriteriaModel {

    private String description;
    private String docType;
}
