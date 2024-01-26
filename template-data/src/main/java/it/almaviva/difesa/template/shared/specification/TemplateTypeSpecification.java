package it.almaviva.difesa.template.shared.specification;



import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.Predicate;

import it.almaviva.difesa.template.shared.util.AppConstants;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import it.almaviva.difesa.shared.data.specification.GenericSpecification;
import it.almaviva.difesa.shared.data.util.DataUtilsMethod;
import it.almaviva.difesa.template.shared.criteria.TemplateTypeCriteria;
import it.almaviva.difesa.template.templateType.entity.shared.TemplateType;

@Component
public class TemplateTypeSpecification implements GenericSpecification<TemplateType, TemplateTypeCriteria> {

    /***
     * Method to apply some optional criteria in the search
     * @param templateTypeCriteria optional criteria
     * @return
     */
    @Override
    public Specification<TemplateType> getSpecification(TemplateTypeCriteria templateTypeCriteria) {
        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            DataUtilsMethod.addFieldToPredicatesIfNotEmptyOrBlank(root, criteriaBuilder, templateTypeCriteria.getDescription(), AppConstants.TEMPLATE_TYPE_DESCRIPTION)
                    .ifPresent(predicates::add);

            DataUtilsMethod.addFieldToPredicatesIfNotEmptyOrBlank(root, criteriaBuilder, templateTypeCriteria.getDocType(), AppConstants.TEMPLATE_TYPE_DOC_TYPE)
                    .ifPresent(predicates::add);

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
