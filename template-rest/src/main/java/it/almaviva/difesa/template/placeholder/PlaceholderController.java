package it.almaviva.difesa.template.placeholder;


import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.almaviva.difesa.template.placeholder.service.PlaceholderService;
import it.almaviva.difesa.template.shared.criteria.PlaceholderCriteria;
import it.almaviva.difesa.template.placeholder.dto.response.PlaceholderFilterResponseSearchDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import java.util.List;


import static it.almaviva.difesa.template.shared.util.AppConstants.PLACEHOLDER_URL;

@RestController
@RequestMapping((PLACEHOLDER_URL))
@Tag(name = "Placeholder API")
@RequiredArgsConstructor
@Validated
public class PlaceholderController {


    private final PlaceholderService placeholderService;


    /***
     * Method to search placeholders with some criteria
     * @param placeholderCriteria criteria
     * @return
     */
    @PostMapping("/filter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Placeholders found"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<List<PlaceholderFilterResponseSearchDTO>> searchPlaceholdersByFilter(
            @RequestBody @Valid PlaceholderCriteria placeholderCriteria) {
        return ResponseEntity.ok(placeholderService.filterPlaceholders(placeholderCriteria));
    }
}
