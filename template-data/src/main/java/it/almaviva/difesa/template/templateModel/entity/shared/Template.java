package it.almaviva.difesa.template.templateModel.entity.shared;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import it.almaviva.difesa.shared.data.entity.GenericEntity;
import it.almaviva.difesa.shared.data.util.SortConstant;
import it.almaviva.difesa.shared.data.util.Sortable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity
@Table(name = "TB_TRTEM_TEMPLATE")
@Getter
@ToString
@NoArgsConstructor
@Setter
@EntityListeners(AuditingEntityListener.class)
public class Template implements GenericEntity, Sortable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "templateGenerator")
	@SequenceGenerator(name = "templateGenerator", sequenceName = "template_sequence", allocationSize = 1)
	@Column(name= "TRTEM_TEMPLATE_PK", nullable = false, length = 50)
	private Long id;

	@Column(name = "TRTEM_NOME_MODELLO", nullable = false, length = 50)
	private String name;
	
	@Column(name = "TRTEM_FORMATO", nullable = false, length = 50)
	private String format;
	
	@Column(name = "TRTEM_TIPO_MODELLO_PK", nullable = false, length = 50)
	private Long templateTypeId;

	@Column(name = "TRTEM_AUTORE_CREAZIONE", nullable = false, length = 50)
	private String author;

	@CreatedDate
	@Column(name = "TRTEM_DATA_CREAZIONE", nullable = false)
	private LocalDateTime creationDate;

	@Column(name = "TRTEM_AUTORE_AGGIORNAMENTO", length = 50)
	private String updateAuthor;

	@LastModifiedDate
	@Column(name = "TRTEM_DATA_AGGIORNAMENTO", nullable = false)
	private LocalDateTime updateDate;
	
	@Column(name = "TRTEM_DATA_INIZIO_VALIDITA", nullable = false)
	private LocalDate validityStartDate;
	
	@Column(name = "TRTEM_DATA_FINE_VALIDITA", nullable = false)
	private LocalDate validityEndDate;
	
	@Column(name = "TRTEM_SORGENTE_HTML", nullable = false)
	private String htmlSource;

	@Column(name = "TRTEM_PLACEHOLDERS", length = 5000)
	private String placeholders;

	@Override
	public Sort getSort() {
		return SortConstant.SORT_BY_LAST_UPDATED_DATE;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
		Template template = (Template) o;
		return id != null && Objects.equals(id, template.id);
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}

}
