package it.almaviva.difesa.template.templateType.entity.shared;

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
@Table(name = "TP_TRTMO_TIPO_MODELLO")
@Getter
@ToString
@NoArgsConstructor
@Setter
@EntityListeners(PreventAnyDataManipulation.class)
public class TemplateType implements GenericEntity, Sortable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "modelGenerator")
    @SequenceGenerator(name = "modelGenerator", sequenceName = "model_sequence", allocationSize = 1)
    @Column(name= "TRTMO_TIPO_MODELLO_PK", nullable = false, length = 50)
    private Long id;

    @Column(name = "TRTMO_DESCRIZIONE", nullable = false, length = 50)
    private String description;

    @Column(name = "trtmo_docat_acr_cat", nullable = false, length = 30)
    private String docAcr;

    @Column(name = "trtmo_dotip_cod", nullable = false, length = 4)
    private String docType;

    @Override
    public Sort getSort() {
        return SortConstant.SORT_BY_NAME;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        TemplateType type = (TemplateType) o;
        return id != null && Objects.equals(id, type.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
