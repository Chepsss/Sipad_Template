package it.almaviva.difesa.template.templateModel.repository;

import org.springframework.stereotype.Repository;

import it.almaviva.difesa.shared.data.repository.GenericRepository;
import it.almaviva.difesa.shared.data.repository.GenericSearchRepository;
import it.almaviva.difesa.template.templateModel.entity.shared.Template;
import java.util.List;

@Repository
public interface TemplateRepository extends GenericRepository<Template, Long>, GenericSearchRepository<Template> {

    List<Template> findByNameAndTemplateTypeId(String name, Long templateTypeId);

    List<Template> findByIdNotAndNameAndTemplateTypeId(Long templateId, String name, Long templateTypeId);

    Template findByName(String nameTemplate);

}
