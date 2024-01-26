package it.almaviva.difesa.template.templateType.dto.abstraction;

import it.almaviva.difesa.shared.common.dto.GenericResponseDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TemplateTypeDTO implements GenericResponseDTO {

    private Long id;
    private String description;
    private String docAcr;
    private String docType;
}
