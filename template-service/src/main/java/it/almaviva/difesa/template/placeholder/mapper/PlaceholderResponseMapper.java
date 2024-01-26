package it.almaviva.difesa.template.placeholder.mapper;


import org.mapstruct.Mapper;

import it.almaviva.difesa.shared.integration.mapper.GenericResponseMapper;
import it.almaviva.difesa.template.placeholder.dto.response.PlaceholderFilterResponseSearchDTO;
import it.almaviva.difesa.template.placeholder.entity.shared.Placeholder;


@Mapper(componentModel = "spring")
public interface PlaceholderResponseMapper extends GenericResponseMapper<Placeholder, PlaceholderFilterResponseSearchDTO> {

}
