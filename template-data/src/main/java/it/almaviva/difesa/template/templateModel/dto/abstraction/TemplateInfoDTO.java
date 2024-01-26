package it.almaviva.difesa.template.templateModel.dto.abstraction;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;

import it.almaviva.difesa.shared.common.dto.GenericResponseDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TemplateInfoDTO implements GenericResponseDTO {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long id;
    private String name;
    private String format;
    private String author;
    private LocalDate validityStartDate;
    private LocalDate validityEndDate;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String content;
    private LocalDate creationDate;
    private LocalDate updateDate;
}
