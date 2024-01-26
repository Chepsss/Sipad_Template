package it.almaviva.difesa.template.shared.criteria;


import it.almaviva.difesa.shared.data.criteria.GenericCriteriaModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaceholderCriteria implements GenericCriteriaModel {

    private String description;
}
