package it.almaviva.difesa.template.placeholder.entity.shared;


import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.Hibernate;
import org.springframework.data.domain.Sort;

import it.almaviva.difesa.shared.data.entity.GenericEntity;
import it.almaviva.difesa.shared.data.util.PreventAnyDataManipulation;
import it.almaviva.difesa.shared.data.util.SortConstant;
import it.almaviva.difesa.shared.data.util.Sortable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity
@Table(name = "TP_TRPLA_PLACEHOLDER")
@Getter
@ToString
@NoArgsConstructor
@Setter
@EntityListeners(PreventAnyDataManipulation.class)
public class Placeholder implements GenericEntity, Sortable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "placeholderGenerator")
    @SequenceGenerator(name = "placeholderGenerator", sequenceName = "placeholder_sequence", allocationSize = 1)
    @Column(name= "TRPLA_PLACEHOLDER_PK", nullable = false, length = 50)
    private Long id;

    @Column(name = "TRPLA_CODICE", nullable = false, length = 50)
    private String code;

    @Column(name = "TRPLA_DESCRIZIONE", nullable = false, length = 50)
    private String description;

    @Column(name = "TRPLA_COD_INTERNO", nullable = false, unique = true, length = 50)
    private String internalCode;


    @Override
    public Sort getSort() {
        return SortConstant.SORT_BY_DESCRIPTION;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Placeholder placeholder = (Placeholder) o;
        return id != null && Objects.equals(id, placeholder.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
