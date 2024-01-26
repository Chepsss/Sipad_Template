package it.almaviva.difesa.template.templateModel.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;
import freemarker.core.InvalidReferenceException;
import freemarker.core.NonHashException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import it.almaviva.difesa.shared.common.utils.StatusSharedEnum;
import it.almaviva.difesa.shared.data.dto.DocumentContentRequestDTO;
import it.almaviva.difesa.shared.service.utils.LambdaUtils;
import it.almaviva.difesa.template.placeholder.repository.PlaceholderRepository;
import it.almaviva.difesa.template.service.MSSipadService;
import it.almaviva.difesa.template.shared.criteria.TemplateCriteria;
import it.almaviva.difesa.template.shared.dto.MatchPlaceholdersDTO;
import it.almaviva.difesa.template.shared.dto.PlaceholderDTO;
import it.almaviva.difesa.template.shared.specification.TemplateSpecification;
import it.almaviva.difesa.template.shared.util.StatusEnum;
import it.almaviva.difesa.template.templateModel.dto.request.TemplateGenerationDTO;
import it.almaviva.difesa.template.templateModel.dto.request.TemplateRequestCreateRequestDTO;
import it.almaviva.difesa.template.templateModel.dto.request.TemplateRequestUpdateRequestDTO;
import it.almaviva.difesa.template.templateModel.dto.response.DocumentDTO;
import it.almaviva.difesa.template.templateModel.dto.response.TemplateFilterResponseSearchDTO;
import it.almaviva.difesa.template.templateModel.dto.response.TemplateResponseDTO;
import it.almaviva.difesa.template.templateModel.mapper.TemplateFilterResponseMapper;
import it.almaviva.difesa.template.templateModel.mapper.TemplateRequestMapper;
import it.almaviva.difesa.template.templateModel.mapper.TemplateResponseMapper;
import it.almaviva.difesa.template.templateModel.repository.TemplateRepository;
import it.almaviva.difesa.template.templateModel.service.TemplateService;
import it.almaviva.difesa.template.templateType.dto.abstraction.TemplateTypeDTO;
import it.almaviva.difesa.template.templateType.mapper.TemplateTypeResponseMapper;
import it.almaviva.difesa.template.templateType.repository.TemplateTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TemplateServiceImpl implements TemplateService {

    private static final String HTML_FORMAT = "<html><head><title></title><style>%s</style></head><body>%s</body></html>";

    private final TemplateRepository templateRepository;
    private final TemplateTypeRepository templateTypeRepository;
    private final TemplateRequestMapper templateRequestMapper;
    private final TemplateResponseMapper templateResponseMapper;
    private final Configuration cfg;
    private final PlaceholderRepository placeholderRepository;
    private final TemplateTypeResponseMapper templateTypeResponseMapper;
    private final TemplateSpecification templateSpecification;
    private final TemplateFilterResponseMapper templateFilterResponseMapper;
    private final MSSipadService msSipadService;
    private final ObjectMapper objectMapper;

    @Value("classpath:fonts/*")
    private Resource[] fontsDirectory;

    @PostConstruct
    public void init() {
        cfg.setDefaultEncoding(StandardCharsets.UTF_8.name());
    }

    /***
     * Method to create new template. This checks if template type id exists
     * @param templateRequestCreateDTO data to process
     */
    @Override
    public void createTemplate(TemplateRequestCreateRequestDTO templateRequestCreateDTO, Long authorId) throws URISyntaxException {
        assertNameTypeOfTemplate(null, templateRequestCreateDTO.getName(), templateRequestCreateDTO.getTemplateTypeId());
        var author = msSipadService.findUserById(authorId);
        LambdaUtils.findByOneOrMoreParameters(templateTypeRepository, repo -> repo.findById(templateRequestCreateDTO.getTemplateTypeId()));
        LocalDate openEndValidity = LocalDate.now().withYear(9999).withMonth(12).withDayOfMonth(31);
        if (templateRequestCreateDTO.getValidityEndDate() != null && templateRequestCreateDTO.getValidityEndDate().isBefore(templateRequestCreateDTO.getValidityStartDate())) {
            log.error("End date " + templateRequestCreateDTO.getValidityEndDate() + " is before start date: " + templateRequestCreateDTO.getValidityStartDate());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, StatusSharedEnum.END_DATE_BEFORE_START_DATE.getNameMessage());
        }
        it.almaviva.difesa.template.templateModel.entity.shared.Template template = templateRequestMapper.asEntity(templateRequestCreateDTO);

        Set<it.almaviva.difesa.template.shared.dto.PlaceholderDTO> placeholdersSet = new HashSet<>();

        smembraHtmlSourceEricavaListaPlaceholder(template.getHtmlSource(), placeholdersSet);

        String placeholders;
        try {
            placeholders = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(placeholdersSet);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Si è verificato un errore nel processing del json placeholders", e);
        }

        template.setPlaceholders(placeholders);

        String authorName = author.getSg155Nome() + " " + author.getSg155Cognome();
        template.setAuthor(authorName);
        template.setUpdateAuthor(authorName);
        if (templateRequestCreateDTO.getValidityEndDate() == null) {
            template.setValidityEndDate(openEndValidity);
        } else {
            template.setValidityEndDate(templateRequestCreateDTO.getValidityEndDate());
        }
        templateRepository.save(template);
    }

    /***
     * Method to check if a template exists in db and in positive case method deletes it
     * @param templateId id of template
     */
    @Override
    public void deleteTemplate(Long templateId) {
        var template = LambdaUtils.findByOneOrMoreParameters(templateRepository, repo -> repo.findById(templateId));
        templateRepository.delete(template);
    }

    /***
     * Method to update a template after check if it exists
     * @param templateId id of template
     * @param templateRequestUpdateDTO data to process
     */
    @Override
    public TemplateResponseDTO updateTemplate(Long templateId, TemplateRequestUpdateRequestDTO templateRequestUpdateDTO, Long authorUpdateId) throws URISyntaxException {
        assertNameTypeOfTemplate(templateId, templateRequestUpdateDTO.getName(), templateRequestUpdateDTO.getTemplateTypeId());
        var authorUpdate = msSipadService.findUserById(authorUpdateId);
        var template = LambdaUtils.findByOneOrMoreParameters(templateRepository, repo -> repo.findById(templateId));
        var templateType = LambdaUtils.findByOneOrMoreParameters(templateTypeRepository, repo -> repo.findById(templateRequestUpdateDTO.getTemplateTypeId()));
        var nameAuthorUpdate = authorUpdate.getSg155Nome() + " " + authorUpdate.getSg155Cognome();
        template.setUpdateAuthor(nameAuthorUpdate);
        template.setName(templateRequestUpdateDTO.getName().trim());
        template.setFormat(templateRequestUpdateDTO.getFormat());
        template.setTemplateTypeId(templateType.getId());
        template.setValidityStartDate(templateRequestUpdateDTO.getValidityStartDate());

        if (templateRequestUpdateDTO.getValidityEndDate() != null) {
            template.setValidityEndDate(templateRequestUpdateDTO.getValidityEndDate());
        } else {
            template.setValidityEndDate(LocalDate.of(9999, 12, 31));
        }

        template.setHtmlSource(templateRequestUpdateDTO.getContent());
        Set<PlaceholderDTO> placeholdersSet = new HashSet<>();

        smembraHtmlSourceEricavaListaPlaceholder(template.getHtmlSource(), placeholdersSet);

        String placeholders;
        try {
            placeholders = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(placeholdersSet);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Si è verificato un errore nel processing del json placeholders", e);
        }
        template.setPlaceholders(placeholders);

        return templateResponseMapper.asDTO(templateRepository.save(template));
    }

    private void assertNameTypeOfTemplate(Long templateId, String name, Long typeId) {
        List<it.almaviva.difesa.template.templateModel.entity.shared.Template> temp;
        if (templateId != null) {
            temp = templateRepository.findByIdNotAndNameAndTemplateTypeId(templateId, name.trim(), typeId);
        } else {
            temp = templateRepository.findByNameAndTemplateTypeId(name.trim(), typeId);
        }
        if (!temp.isEmpty()) {
            log.error("Esiste già un template con lo stesso nome e la stessa tipologia");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, StatusEnum.TEMPLATE_NAME_AND_TYPE_ALREADY_EXITST.getNameMessage());
        }
    }

    public void getTemplateById(Long templateId, HttpServletResponse response) {
        var templateRecord = LambdaUtils.findByOneOrMoreParameters(templateRepository, repo -> repo.findById(templateId));
        var nameTemplate = templateRecord.getName();
        var htmlString = templateRecord.getHtmlSource();
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        try (Writer out = new OutputStreamWriter(ba)) {
            Template template = new Template(nameTemplate, new StringReader(htmlString), cfg);
            Map<String, Object> templateData = getAllPlaceholdersWithDescriptions(htmlString);
            template.process(templateData, out);
            out.flush();
            final String DEST = "C:\\Users\\Adria\\Desktop\\ARQ\\test.pdf";
            HtmlConverter.convertToPdf(ba.toString(StandardCharsets.UTF_8), new FileOutputStream(DEST));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, StatusEnum.INTERNAL_SERVER_ERROR.getNameMessage());
        }
    }

    @Override
    public DocumentDTO generate(TemplateGenerationDTO templateGenerationDTO, HttpServletResponse response) {
        List<String> missingTemplateVariables = getMissingTemplateVariables(templateGenerationDTO);
        var templateRecord = LambdaUtils.findByOneOrMoreParameters(templateRepository, repo -> repo.findById(templateGenerationDTO.getTemplateId()));
        String nameTemplate = templateRecord.getName();
        String htmlString = templateRecord.getHtmlSource();
        String styleCss = templateGenerationDTO.getStyleCss();
        return doGenerate
                (templateGenerationDTO, missingTemplateVariables, nameTemplate, htmlString, styleCss);
    }

    @Override
    public DocumentDTO generateFromFile(TemplateGenerationDTO templateGenerationDTO, HttpServletResponse response) {
        String htmlString = templateGenerationDTO.getFile();
        String styleCss = templateGenerationDTO.getStyleCss();
        ArrayList<String> missingTemplateVariables = doGetMissingPlaceholders(templateGenerationDTO, htmlString, "default");
        return doGenerate
                (templateGenerationDTO, missingTemplateVariables, "default", htmlString, styleCss);
    }

    private DocumentDTO doGenerate(TemplateGenerationDTO templateGenerationDTO, List<String> missingTemplateVariables,
                                   String nameTemplate, String htmlString, String styleCss) {
        String BOM = "\uFEFF";
        htmlString = htmlString.replaceAll(BOM, "");
        if (Objects.nonNull(styleCss)) {
            htmlString = String.format(HTML_FORMAT, styleCss, htmlString);
        } else {
            String stringOfFileCss = getStringOfFileStyleCss();
            htmlString = String.format(HTML_FORMAT, stringOfFileCss, htmlString);
        }
        Writer out = new StringWriter();
        HashMap<String, Object> model = templateGenerationDTO.getModel();
        String fileContent;
        try {
            Template template = new Template(nameTemplate, new StringReader(htmlString), cfg);
            template.process(model, out);
            fileContent = out.toString();
            out.flush();
        } catch (InvalidReferenceException e) {
            fileContent = "";
        } catch (IOException | TemplateException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, StatusEnum.INTERNAL_SERVER_ERROR.getNameMessage());
        }
        DocumentDTO documentDTO = new DocumentDTO();
        documentDTO.setPlaceholderErrors((ArrayList<String>) missingTemplateVariables);
        documentDTO.setError(!templateGenerationDTO.getForce() && !missingTemplateVariables.isEmpty());
        if (Boolean.TRUE.equals(templateGenerationDTO.getIsPdf())) {
            documentDTO.setFile(convertToPdf(fileContent));
        } else {
            documentDTO.setFile(Base64.getEncoder().encodeToString(fileContent.getBytes(StandardCharsets.UTF_8)));
        }
        return documentDTO;
    }

    public String getStringOfFileStyleCss() {
        String fileName = "css/style-template.css";
        String fileInString = null;
        try {
            try (InputStream resource = this.getClass().getClassLoader().getResourceAsStream(fileName)) {
                if (resource != null)
                    fileInString = IOUtils.toString(resource, StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            log.error("File css not found", e);
            return null;
        }
        return fileInString;
    }

    public String convertToPdf(String fileContent) {
        ByteArrayOutputStream target = new ByteArrayOutputStream();

        ConverterProperties properties = new ConverterProperties();
        DefaultFontProvider fontProvider = new DefaultFontProvider();
        Arrays.stream(fontsDirectory).forEach(resource -> {
            try {
                try (InputStream input = resource.getInputStream()){
                    fontProvider.addFont(input.readAllBytes(), StandardCharsets.UTF_8.name());
                }
            } catch (IOException e) {
                log.error("Error in loading fonts", e);
            }
        });
        properties.setFontProvider(fontProvider);

        HtmlConverter.convertToPdf(fileContent, target, properties);

        return Base64.getEncoder().encodeToString(target.toByteArray());
    }

    public List<String> getMissingTemplateVariables(TemplateGenerationDTO templateGenerationDTO) {
        var templateRecord = LambdaUtils.findByOneOrMoreParameters(templateRepository, repo -> repo.findById(templateGenerationDTO.getTemplateId()));
        String htmlString = templateRecord.getHtmlSource();
        String nameTemplate = templateRecord.getName();
        return doGetMissingPlaceholders(templateGenerationDTO, htmlString, nameTemplate);
    }

    private ArrayList<String> doGetMissingPlaceholders(TemplateGenerationDTO templateGenerationDTO, String htmlString, String nameTemplate) {
        StringWriter stringWriter = new StringWriter();
        Map<String, Object> dataModel;
        if (Boolean.TRUE.equals(templateGenerationDTO.getForce())) {
            dataModel = templateGenerationDTO.getModel();
        } else {
            dataModel = new HashMap<>(templateGenerationDTO.getModel());
        }
        ArrayList<String> notFounds = new ArrayList<>();
        boolean exceptionCaught;

        do {
            exceptionCaught = false;
            try {
                Template template = new Template(nameTemplate, new StringReader(htmlString), cfg);
                template.process(dataModel, stringWriter);
            } catch (InvalidReferenceException e) {
                log.error(">>>>>>>> Error: {}", e.getBlamedExpressionString());
                exceptionCaught = true;
                forceValue(dataModel, e.getBlamedExpressionString());
                notFounds.add(e.getBlamedExpressionString());
            } catch (NonHashException e) {
                log.error(">>>>>>>> Error: {}", e.getBlamedExpressionString());
                exceptionCaught = true;
                forceHash(dataModel, e.getBlamedExpressionString());
                notFounds.add(e.getBlamedExpressionString());
            } catch (IOException | TemplateException e) {
                log.error(">>>>>>>> Error", e);
                exceptionCaught = true;
                forceHash(dataModel, e.getMessage());
                notFounds.add(e.getMessage());
            }
        } while (exceptionCaught);

        return notFounds;
    }

    private void forceHash(Map<String, Object> dataModel, String s) {
        force(dataModel, s, new HashMap<String, Object>());
    }

    private void forceValue(Map<String, Object> dataModel, String s) {
        force(dataModel, s, "");
    }

    private void force(Map<String, Object> dataModel, String s, Object value) {
        if (s == null || "".equals(s.trim())) {
            return;
        }
        if (!s.contains(".")) {
            dataModel.put(s, value);
            return;
        }
        String[] keys = s.split("[.]");
        HashMap<String, Object> map = (HashMap<String, Object>) dataModel;
        for (int i = 0; i < keys.length - 1; i++) {
            map = (HashMap<String, Object>) map.get(keys[i]);
        }
        map.put(keys[keys.length - 1], value);
    }

    /***
     * Method to search all placeholders on html string and put relative description
     * for final pdf template
     * @param htmlString string with html code
     * @return
     */
    private Map<String, Object> getAllPlaceholdersWithDescriptions(String htmlString) {
        Map<String, Object> templateData = new HashMap<>();
        List<String> placeholders = new ArrayList<>();
        int startIndex = -1;
        int endIndex = -1;
        for (int i = 0; i < htmlString.length(); i++) {
            if (htmlString.charAt(i) == '$') {
                startIndex = i;
            }
            if (htmlString.charAt(i) == '}') {
                endIndex = i;
                placeholders.add(htmlString.substring(startIndex, endIndex + 1));
            }
        }
        placeholders.forEach(x -> {
            String description = placeholderRepository.findByCode(x).getDescription();
            templateData.put(x.substring(2, x.length() - 1), description);
        });
        return templateData;
    }

    /***
     * Method to search templates with some criterias
     * @param templateCriteria criteria of search
     * @param pageable criteria for pagination and sorting of elements
     * @return
     */
    @Override
    public Page<TemplateFilterResponseSearchDTO> filterTemplates(TemplateCriteria templateCriteria, Pageable pageable) {
        Specification<it.almaviva.difesa.template.templateModel.entity.shared.Template> filterSpecification = templateSpecification.getSpecification(templateCriteria);
        Page<it.almaviva.difesa.template.templateModel.entity.shared.Template> entities = templateRepository.findAll(filterSpecification, pageable);
        if (CollectionUtils.isEmpty(entities.getContent())) {
            return Page.empty();
        }
        var templateTypesDTO = getDescriptionTemplateTypeOfSavedTemplates(entities);
        return entities.map(x -> {
            var templateDTO = templateFilterResponseMapper.asDTO(x);
            if (templateTypesDTO.size() > 0 && templateTypesDTO.containsKey(templateDTO.getId())) {
                templateDTO.setTemplateTypeDescription(templateTypesDTO.get(templateDTO.getId()).getDescription());
            }
            return templateDTO;
        });
    }

    /***
     * Method to obtain
     * @param templateId
     * @return
     */
    @Override
    public TemplateResponseDTO getTemplateById(Long templateId) {
        var template = LambdaUtils.findByOneOrMoreParameters(templateRepository, repo -> repo.findById(templateId));
        LambdaUtils.findByOneOrMoreParameters(templateTypeRepository, repo -> repo.findById(template.getTemplateTypeId()));
        var output = templateResponseMapper.asDTO(template);
        output.setContent(template.getHtmlSource());
        return output;
    }

    @Override
    public TemplateResponseDTO getTemplateByName(TemplateCriteria templateCriteria) {
        var template = templateRepository.findByName(templateCriteria.getName());
        var output = templateResponseMapper.asDTO(template);
        output.setContent(template.getHtmlSource());
        return output;
    }

    /***
     * Method to get all template
     * @return
     */
    @Override
    public List<TemplateFilterResponseSearchDTO> getAllTemplate() {
        List<TemplateFilterResponseSearchDTO> output = new ArrayList<>();
        List<it.almaviva.difesa.template.templateModel.entity.shared.Template> entities = templateRepository.findAll();
        if (!CollectionUtils.isEmpty(entities)) {
            output.addAll(templateFilterResponseMapper.asDTOList(entities));
        }
        return output;
    }

    /***
     * Method to replace all placeholder and generate a string of document
     * @param documentContentRequestDTO
     * @return
     */
    @Override
    public String replacePlaceholdersAndGenerateString(DocumentContentRequestDTO documentContentRequestDTO) {
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        try (Writer out = new OutputStreamWriter(ba)) {
            Template template = new Template(documentContentRequestDTO.getFileName(), new StringReader(documentContentRequestDTO.getFileWithPlaceholders()), cfg);
            Map<String, Object> templateData = getAllPlaceholdersWithDescriptions(documentContentRequestDTO.getFileWithPlaceholders());
            template.process(templateData, out);
            out.flush();
            return ba.toString(StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, StatusEnum.INTERNAL_SERVER_ERROR.getNameMessage());
        }
    }

    /***
     * Method to retrieve the template and template type pairs and to register of them id
     * and convert the second of pair into TemplateTypeDTO
     * @param templates list of saved templates
     * @return
     */
    private Map<Long, TemplateTypeDTO> getDescriptionTemplateTypeOfSavedTemplates(Page<it.almaviva.difesa.template.templateModel.entity.shared.Template> templates) {
        Map<Long, TemplateTypeDTO> output = new HashMap<>();
        templates.getContent().forEach(x ->
                output.put(x.getId(), templateTypeResponseMapper.asDTO(LambdaUtils.findByOneOrMoreParameters(templateTypeRepository, repo -> repo.findById(x.getTemplateTypeId()))))
        );
        return output;
    }

    @Override
    public MatchPlaceholdersDTO placeholdersDoMatch(String htmlSource, Long templateId) {

        MatchPlaceholdersDTO response = new MatchPlaceholdersDTO();

        var template = LambdaUtils.findByOneOrMoreParameters(templateRepository, repo -> repo.findById(templateId));

        //Recupero i placeholder del html string in input
        Set<PlaceholderDTO> placeholdersDelDocumento = new HashSet<>();
        smembraHtmlSourceEricavaListaPlaceholder(htmlSource, placeholdersDelDocumento);

        //Recupero i placeholder del template
        Set<PlaceholderDTO> placeholdersDelTemplate = new HashSet<>();
        if (template.getPlaceholders() != null) {
            try {
                placeholdersDelTemplate = objectMapper.readValue(template.getPlaceholders(), new TypeReference<>() {
                });
            } catch (JsonProcessingException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Errore parsing del json dei placeholders del template", e);
            }
        }

        //Verifico che i placeholder dei template non sian stati cancellati dall'html source in quanto questi non possono essere eliminati
        if (this.placeHoldersDelTemplateCancellati(placeholdersDelTemplate, placeholdersDelDocumento)) {
            String nomiPlaceholders = placeholdersDelTemplate.stream()
                    .map(PlaceholderDTO::getPlaceHolder).collect(Collectors.joining(", "));
            response.setPlaceholdersDoMatch(false);
            response.setNomiPlaceholders(nomiPlaceholders);
        }
        return response;
    }

    private boolean placeHoldersDelTemplateCancellati(Set<PlaceholderDTO> placeholdersDelTemplate, Set<PlaceholderDTO> placeholdersDelDocumento) {
        AtomicBoolean placeholdersEliminati = new AtomicBoolean(false);
        placeholdersDelTemplate.forEach(
                placeholder -> {
                    if (!placeholdersDelDocumento.contains(placeholder)) {
                        placeholdersEliminati.set(true);
                    }
                });
        return placeholdersEliminati.get();
    }

    private void smembraHtmlSourceEricavaListaPlaceholder(String htmlSource, Set<PlaceholderDTO> placeholdersList) {

        if (!htmlSource.isBlank()) {

            String[] htmlList = htmlSource.split(" ");

            for (String s : htmlList) {
                if (s.contains("${")) {
                    var placeholder = new PlaceholderDTO();
                    placeholder.setPlaceHolder(s.substring(s.indexOf("$"), s.indexOf("}") + 1));
                    placeholdersList.add(placeholder);
                }
            }

            log.info("PlaceholderList: {}", placeholdersList);

        }
    }

}
