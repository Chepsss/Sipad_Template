package it.almaviva.difesa.template.shared.specification;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import it.almaviva.difesa.shared.data.specification.GenericSpecification;
import it.almaviva.difesa.shared.data.util.DataUtilsMethod;
import it.almaviva.difesa.template.placeholder.entity.shared.Placeholder;
import it.almaviva.difesa.template.shared.criteria.PlaceholderCriteria;

@Component
public class PlaceholderSpecification implements GenericSpecification<Placeholder, PlaceholderCriteria> {

    /***
     * Method to apply some optional criteria in the search
     * @param placeholderCriteria optional criteria
     * @return
     */
    @Override
    public Specification<Placeholder> getSpecification(PlaceholderCriteria placeholderCriteria) {
        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            DataUtilsMethod.addFieldToPredicatesIfNotEmptyOrBlank(root, criteriaBuilder, placeholderCriteria.getDescription(), "description")
                    .ifPresent(predicates::add);

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
