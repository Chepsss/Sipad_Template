package it.almaviva.difesa.template.templateModel.dto.request;

import it.almaviva.difesa.shared.common.dto.GenericRequestDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;

@Getter
@Setter
@NoArgsConstructor
public class TemplateGenerationDTO implements GenericRequestDTO {
    private HashMap<String, Object> model;
    private Long templateId;
    private Boolean isPdf;
    private Boolean force;
    private String file;
    private String styleCss;
}
