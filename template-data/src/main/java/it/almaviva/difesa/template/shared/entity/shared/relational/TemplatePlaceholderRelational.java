package it.almaviva.difesa.template.shared.entity.shared.relational;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import org.hibernate.Hibernate;

import it.almaviva.difesa.shared.data.entity.GenericEntity;
import it.almaviva.difesa.template.shared.entity.shared.composite.TemplatePlaceholderCompositeKey;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "TR_TRMOP_MODELLO_PLACEHOLDER")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@IdClass(TemplatePlaceholderCompositeKey.class)
public class TemplatePlaceholderRelational implements GenericEntity {

    @Id
    @Column(name = "TRMOP_TIPO_MODELLO_PK")
    private Long templateId;

    @Id
    @Column(name = "TRMOP_PLACEHOLDER_PK")
    private Long placeholderId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        TemplatePlaceholderRelational that = (TemplatePlaceholderRelational) o;
        return templateId != null && Objects.equals(templateId, that.templateId)
                && placeholderId != null && Objects.equals(placeholderId, that.placeholderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(templateId, placeholderId);
    }
}
