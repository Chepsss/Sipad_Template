package it.almaviva.difesa.template.templateModel.service;

import it.almaviva.difesa.shared.data.dto.DocumentContentRequestDTO;
import it.almaviva.difesa.template.shared.criteria.TemplateCriteria;
import it.almaviva.difesa.template.shared.dto.MatchPlaceholdersDTO;
import it.almaviva.difesa.template.templateModel.dto.request.TemplateGenerationDTO;
import it.almaviva.difesa.template.templateModel.dto.request.TemplateRequestCreateRequestDTO;
import it.almaviva.difesa.template.templateModel.dto.request.TemplateRequestUpdateRequestDTO;
import it.almaviva.difesa.template.templateModel.dto.response.DocumentDTO;
import it.almaviva.difesa.template.templateModel.dto.response.TemplateFilterResponseSearchDTO;
import it.almaviva.difesa.template.templateModel.dto.response.TemplateResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.net.URISyntaxException;
import java.util.List;

public interface TemplateService {

    void deleteTemplate(Long templateId);

    TemplateResponseDTO updateTemplate(Long templateId, TemplateRequestUpdateRequestDTO templateRequestUpdateDTO, Long authorUpdateId) throws URISyntaxException;

    void createTemplate(TemplateRequestCreateRequestDTO templateRequestCreateDTO, Long authorId) throws URISyntaxException;

    void getTemplateById(Long templateId, HttpServletResponse response);

    DocumentDTO generateFromFile(TemplateGenerationDTO templateGenerationDTO, HttpServletResponse response);

    Page<TemplateFilterResponseSearchDTO> filterTemplates(TemplateCriteria templateCriteria, Pageable pageable);

    TemplateResponseDTO getTemplateById(Long templateId);

    DocumentDTO generate(TemplateGenerationDTO templateGenerationDTO, HttpServletResponse response);

    List<TemplateFilterResponseSearchDTO> getAllTemplate();

    String replacePlaceholdersAndGenerateString(DocumentContentRequestDTO documentContentRequestDTO);

    String convertToPdf(String fileContent);

    MatchPlaceholdersDTO placeholdersDoMatch(String htmlSource, Long templateId);

    TemplateResponseDTO getTemplateByName(TemplateCriteria templateCriteria);

}
