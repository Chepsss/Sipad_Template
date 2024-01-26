package it.almaviva.difesa.template.templateType.service.query.impl;


import it.almaviva.difesa.template.shared.criteria.TemplateTypeCriteria;
import it.almaviva.difesa.template.shared.specification.TemplateTypeSpecification;
import it.almaviva.difesa.template.templateType.dto.response.TemplateTypeFilterResponseSearchDTO;
import it.almaviva.difesa.template.templateType.entity.shared.TemplateType;
import it.almaviva.difesa.template.templateType.mapper.TemplateTypeResponseMapper;
import it.almaviva.difesa.template.templateType.repository.TemplateTypeRepository;
import it.almaviva.difesa.template.templateType.service.query.TemplateTypeQueryService;
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
public class TemplateTypeQueryServiceImpl implements TemplateTypeQueryService {


    private final TemplateTypeRepository templateTypeRepository;
    private final TemplateTypeResponseMapper templateTypeResponseMapper;
    private final TemplateTypeSpecification templateTypeSpecification;

    /***
     * Method to filter template types in base of some criteria
     * @param templateTypeCriteria criteria for filtering
     * @return
     */
    @Override
    public List<TemplateTypeFilterResponseSearchDTO> filterTemplateTypes(TemplateTypeCriteria templateTypeCriteria) {
        List<TemplateTypeFilterResponseSearchDTO> output = new ArrayList<>();
        Specification<TemplateType> filterSpecification = templateTypeSpecification.getSpecification(templateTypeCriteria);
        var listTemplateTypes = templateTypeRepository.findAll(filterSpecification);
        if (CollectionUtils.isEmpty(listTemplateTypes)) {
            return output;
        }
        listTemplateTypes.forEach(x->output.add(templateTypeResponseMapper.asDTO(x)));
        return output;
    }
}
