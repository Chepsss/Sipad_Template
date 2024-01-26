package it.almaviva.difesa.template.placeholder.repository;


import org.springframework.stereotype.Repository;

import it.almaviva.difesa.shared.data.repository.GenericRepository;
import it.almaviva.difesa.shared.data.repository.GenericSearchRepository;
import it.almaviva.difesa.template.placeholder.entity.shared.Placeholder;

@Repository
public interface PlaceholderRepository extends GenericRepository<Placeholder, Long>, GenericSearchRepository<Placeholder> {

    Placeholder findByCode(String placeholderCode);
}
