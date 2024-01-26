package it.almaviva.difesa.template.templateModel.dto.request;


import it.almaviva.difesa.template.templateModel.dto.response.TemplateResponseDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TemplateRequestUpdateRequestDTO extends TemplateResponseDTO {

    private String content;

}
