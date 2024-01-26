package it.almaviva.difesa.template.templateModel.dto.response;


import it.almaviva.difesa.template.templateModel.dto.abstraction.TemplateInfoDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TemplateFilterResponseSearchDTO extends TemplateInfoDTO {

    private String templateTypeDescription;
}
