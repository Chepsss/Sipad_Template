package it.almaviva.difesa.template.templateType.repository;

import it.almaviva.difesa.shared.data.repository.GenericRepository;
import it.almaviva.difesa.shared.data.repository.GenericSearchRepository;
import it.almaviva.difesa.template.templateType.entity.shared.TemplateType;

public interface TemplateTypeRepository extends GenericRepository<TemplateType, Long>, GenericSearchRepository<TemplateType> {
}
