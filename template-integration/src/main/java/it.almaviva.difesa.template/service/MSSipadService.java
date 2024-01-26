package it.almaviva.difesa.template.service;

import it.almaviva.difesa.shared.data.dto.VwSg155StgiurFastMiCiDTO;

import java.net.URISyntaxException;


public interface MSSipadService {

    VwSg155StgiurFastMiCiDTO findUserById(Long id)throws URISyntaxException;

}
