package it.almaviva.difesa.template.templateModel;


import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.almaviva.difesa.shared.data.dto.DocumentContentRequestDTO;
import it.almaviva.difesa.template.shared.criteria.TemplateCriteria;
import it.almaviva.difesa.template.shared.dto.MatchPlaceholdersDTO;
import it.almaviva.difesa.template.shared.utils.PrincipalUtilMethod;
import it.almaviva.difesa.template.templateModel.dto.request.ConvertToPdfDTO;
import it.almaviva.difesa.template.templateModel.dto.request.TemplateGenerationDTO;
import it.almaviva.difesa.template.templateModel.dto.request.TemplateRequestCreateRequestDTO;
import it.almaviva.difesa.template.templateModel.dto.request.TemplateRequestUpdateRequestDTO;
import it.almaviva.difesa.template.templateModel.dto.response.DocumentDTO;
import it.almaviva.difesa.template.templateModel.dto.response.TemplateFilterResponseSearchDTO;
import it.almaviva.difesa.template.templateModel.dto.response.TemplateResponseDTO;
import it.almaviva.difesa.template.templateModel.service.TemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;

import static it.almaviva.difesa.template.shared.util.AppConstants.TEMPLATE_URL;

@RestController
@RequestMapping(TEMPLATE_URL)
@Tag(name = "Template API")
@RequiredArgsConstructor
@Slf4j
@Validated
public class TemplateController {

    private final TemplateService templateService;

    @Value("${jwt.header}")
    private String header;


    /***
     * Method to create new template
     *
     * @param templateRequestCreateDTO data to process
     */
    @PutMapping("/create")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Template created"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public void createTemplate(@RequestBody @Valid TemplateRequestCreateRequestDTO templateRequestCreateDTO, HttpServletRequest request) throws URISyntaxException {
        var authorId = Long.valueOf(Objects.requireNonNull(PrincipalUtilMethod.getClaimValueByHeaderAndClaimKey(request, header, "andipId")));
        templateService.createTemplate(templateRequestCreateDTO, authorId);
    }


    /***
     * Method to update already saved template
     *
     * @param templateRequestUpdateDTO data to process
     */
    @PutMapping("/update")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Template updated"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<TemplateResponseDTO> updateTemplate(@RequestParam("templateId") @Min(1) Long templateId, @RequestBody @Valid TemplateRequestUpdateRequestDTO templateRequestUpdateDTO, HttpServletRequest request) throws URISyntaxException {
        var authorUpdateId = Long.valueOf(Objects.requireNonNull(PrincipalUtilMethod.getClaimValueByHeaderAndClaimKey(request, header, "andipId")));
        return ResponseEntity.ok(templateService.updateTemplate(templateId, templateRequestUpdateDTO, authorUpdateId));
    }

    /***
     * Method to delete a template
     *
     * @param templateId if of template to delete
     */
    @DeleteMapping("/delete/{templateId}")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Template deleted"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public void deleteTemplate(@PathVariable @Min(1) Long templateId) {
        templateService.deleteTemplate(templateId);
    }


    // TEST PER VERIFICARE LA FUNZIONALITA' DELLA LIBRERIA FREEMARKER (sostituzione placeholder e produzione pdf)
    @GetMapping(value = "/generate/{templateId}", produces = MediaType.APPLICATION_PDF_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Template found"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public void generateTemplate(@PathVariable @Min(1) Long templateId, HttpServletResponse response) {
        templateService.getTemplateById(templateId, response);
    }

    @PostMapping(value = "/replacePlaceholder", produces = MediaType.TEXT_HTML_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Placeholders replaced"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<String> replacePlaceholderAndGenerateString(@RequestBody @Valid DocumentContentRequestDTO documentContentRequestDTO) {
        return ResponseEntity.ok(templateService.replacePlaceholdersAndGenerateString(documentContentRequestDTO));
    }

    @PostMapping(value = "/generate")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Template found"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<DocumentDTO> generate(@RequestBody @Valid TemplateGenerationDTO templateGenerationDTO, HttpServletResponse response) throws UnsupportedEncodingException {
        return ResponseEntity.ok(templateService.generate(templateGenerationDTO, response));
    }

    @PostMapping(value = "/generateFromFile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Template found"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<DocumentDTO> generateFromFile(@RequestBody @Valid TemplateGenerationDTO templateGenerationDTO, HttpServletResponse response) throws UnsupportedEncodingException {
        return ResponseEntity.ok(templateService.generateFromFile(templateGenerationDTO, response));
    }
    //generateFromFile

    /***
     * Method to search templates with some criteria
     * @param templateCriteria criteria
     * @param pageable criteria for paging and sorting elements
     * @return
     */
    @PostMapping("/filter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Templates found"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<Page<TemplateFilterResponseSearchDTO>> searchTemplatesByFilter(@RequestBody @Valid TemplateCriteria templateCriteria, Pageable pageable) {
        return ResponseEntity.ok(templateService.filterTemplates(templateCriteria, pageable));
    }


    /***
     * Method to get all templates
     * @return
     */
    @GetMapping("/all")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Templates found"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<List<TemplateFilterResponseSearchDTO>> getAllTemplate() {
        return ResponseEntity.ok(templateService.getAllTemplate());
    }


    @GetMapping(value = "/{templateId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Template found"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<TemplateResponseDTO> getTemplateById(@PathVariable @Min(1) Long templateId) {
        return ResponseEntity.ok(templateService.getTemplateById(templateId));
    }

    @PostMapping(value = "/convertToPdf")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Template found"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public String convertToPdf(@RequestBody @Valid ConvertToPdfDTO convertToPdfDTO) {
        return templateService.convertToPdf(convertToPdfDTO.getFileContent());
    }

    /***
     * Method to verify if placeholders contained in the input string (html) match with template ones
     * @param htmlSource criteria
     * @return Boolean
     */
    @PostMapping("/check-if-placeholders-match/{templateId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Placeholders found"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<MatchPlaceholdersDTO> placeholdersDoMatch(
            @RequestBody @NotNull String htmlSource, @PathVariable Long templateId) {
        return ResponseEntity.ok(templateService.placeholdersDoMatch(htmlSource, templateId));
    }

    @PostMapping(value = "/getByName")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Template found"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<TemplateResponseDTO> getTemplateByName(@RequestBody TemplateCriteria templateCriteria) {
        return ResponseEntity.ok(templateService.getTemplateByName(templateCriteria));
    }

}
