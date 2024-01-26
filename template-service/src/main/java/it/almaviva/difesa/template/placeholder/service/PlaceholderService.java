package it.almaviva.difesa.template.placeholder.service;

import it.almaviva.difesa.template.shared.criteria.PlaceholderCriteria;
import it.almaviva.difesa.template.placeholder.dto.response.PlaceholderFilterResponseSearchDTO;
import java.util.List;


public interface PlaceholderService {

    List<PlaceholderFilterResponseSearchDTO> filterPlaceholders(PlaceholderCriteria placeholderCriteria);
}
