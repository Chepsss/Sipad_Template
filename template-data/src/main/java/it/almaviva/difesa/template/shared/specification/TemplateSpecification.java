package it.almaviva.difesa.template.shared.specification;


import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import it.almaviva.difesa.shared.data.specification.GenericSpecification;
import it.almaviva.difesa.shared.data.util.DataUtilsMethod;
import it.almaviva.difesa.template.shared.criteria.TemplateCriteria;
import it.almaviva.difesa.template.templateModel.entity.shared.Template;

@Component
public class TemplateSpecification implements GenericSpecification<Template, TemplateCriteria> {

    /***
     * Method to apply some optional criteria in the search
     * @param templateCriteria optional criteria
     * @return
     */
    @Override
    public Specification<Template> getSpecification(TemplateCriteria templateCriteria) {
        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            DataUtilsMethod.addFieldToPredicatesIfNotEmptyOrBlank(root, criteriaBuilder, templateCriteria.getName(), "name")
                    .ifPresent(predicates::add);
            DataUtilsMethod.addFieldToPredicatesIfNotEmptyOrBlank(root, criteriaBuilder, templateCriteria.getFormat(), "format")
                    .ifPresent(predicates::add);
            DataUtilsMethod.addFieldToPredicatesIfNotEmptyOrBlank(root, criteriaBuilder, templateCriteria.getAuthor(), "author")
                    .ifPresent(predicates::add);
            DataUtilsMethod.addFieldToPredicatesIfNotEmptyOrBlank(root, criteriaBuilder, templateCriteria.getTemplateTypeId(), "templateTypeId")
                    .ifPresent(predicates::add);
            DataUtilsMethod.addFieldToPredicatesIfNotEmptyOrBlank(root, criteriaBuilder, templateCriteria.getValidityEndDate(), "validityEndDate")
                    .ifPresent(predicates::add);
            DataUtilsMethod.addFieldToPredicatesIfNotEmptyOrBlank(root, criteriaBuilder, templateCriteria.getValidityStartDate(), "validityStartDate")
                    .ifPresent(predicates::add);
            if(templateCriteria.getCreationDate()!=null) {
                DataUtilsMethod.addFieldToPredicatesIfNotEmptyOrBlank(root, criteriaBuilder, templateCriteria.getCreationDate().atStartOfDay(), "creationDate")
                        .ifPresent(predicates::add);
            }
            if(templateCriteria.getUpdateDate()!=null) {
                DataUtilsMethod.addFieldToPredicatesIfNotEmptyOrBlank(root, criteriaBuilder, templateCriteria.getUpdateDate().atStartOfDay(), "updateDate")
                        .ifPresent(predicates::add);
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
