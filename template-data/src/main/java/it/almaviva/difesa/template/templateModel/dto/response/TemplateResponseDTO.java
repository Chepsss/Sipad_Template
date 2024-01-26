package it.almaviva.difesa.template.templateModel.dto.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import it.almaviva.difesa.template.templateModel.dto.abstraction.TemplateInfoDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class TemplateResponseDTO extends TemplateInfoDTO {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long templateTypeId;

}
