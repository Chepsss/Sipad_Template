package it.almaviva.difesa.template.templateType;


import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.almaviva.difesa.template.shared.criteria.TemplateTypeCriteria;
import it.almaviva.difesa.template.templateType.dto.response.TemplateTypeFilterResponseSearchDTO;
import it.almaviva.difesa.template.templateType.service.query.TemplateTypeQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import java.util.List;

import static it.almaviva.difesa.template.shared.util.AppConstants.TEMPLATE_TYPE_URL;

@RestController
@RequestMapping((TEMPLATE_TYPE_URL))
@Tag(name = "Template API")
@RequiredArgsConstructor
@Validated
public class TemplateTypeController {


    private final TemplateTypeQueryService templateTypeQueryService;

    /***
     * Method to search template types with some criteria
     * @param templateTypeCriteria criteria
     * @return
     */
    @PostMapping("/filter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Template types found"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<List<TemplateTypeFilterResponseSearchDTO>> searchUsersByFilter(
            @RequestBody @Valid TemplateTypeCriteria templateTypeCriteria) {
        return ResponseEntity.ok(templateTypeQueryService.filterTemplateTypes(templateTypeCriteria));
    }
}
