package it.almaviva.difesa.template.shared.criteria;



import java.time.LocalDate;

import it.almaviva.difesa.shared.data.criteria.GenericCriteriaModel;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class TemplateCriteria implements GenericCriteriaModel {

    private String name;
    private String format;
    private String author;
    private Long templateTypeId;
    private LocalDate validityStartDate;
    private LocalDate validityEndDate;
    private LocalDate creationDate;
    private LocalDate updateDate;

}
