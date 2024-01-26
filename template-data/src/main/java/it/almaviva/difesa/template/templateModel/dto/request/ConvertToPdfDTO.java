package it.almaviva.difesa.template.templateModel.dto.request;

import it.almaviva.difesa.shared.common.dto.GenericRequestDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ConvertToPdfDTO implements GenericRequestDTO {

    private String fileContent;

}
