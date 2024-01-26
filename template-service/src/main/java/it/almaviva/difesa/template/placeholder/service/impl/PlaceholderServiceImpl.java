package it.almaviva.difesa.template.placeholder.service.impl;


import it.almaviva.difesa.template.placeholder.entity.shared.Placeholder;
import it.almaviva.difesa.template.placeholder.mapper.PlaceholderResponseMapper;
import it.almaviva.difesa.template.placeholder.service.PlaceholderService;
import it.almaviva.difesa.template.placeholder.repository.PlaceholderRepository;
import it.almaviva.difesa.template.shared.criteria.PlaceholderCriteria;
import it.almaviva.difesa.template.shared.specification.PlaceholderSpecification;
import it.almaviva.difesa.template.placeholder.dto.response.PlaceholderFilterResponseSearchDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import java.util.ArrayList;
import java.util.List;



@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class PlaceholderServiceImpl implements PlaceholderService {

    private final PlaceholderRepository placeholderRepository;
    private final PlaceholderResponseMapper placeholderResponseMapper;
    private final PlaceholderSpecification placeholderSpecification;

    /***
     * Method to filter placeholder in base of some criteria
     * @param placeholderCriteria criteria for filter
     * @return
     */
    @Override
    public List<PlaceholderFilterResponseSearchDTO> filterPlaceholders(PlaceholderCriteria placeholderCriteria) {
        List<PlaceholderFilterResponseSearchDTO> output = new ArrayList<>();
        Specification<Placeholder> filterSpecification = placeholderSpecification.getSpecification(placeholderCriteria);
        var listPlaceholders = placeholderRepository.findAll(filterSpecification);
        if (CollectionUtils.isEmpty(listPlaceholders)) {
            return output;
        }
        listPlaceholders.forEach(x->output.add(placeholderResponseMapper.asDTO(x)));
        return output;
    }
}
