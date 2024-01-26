package it.almaviva.difesa.template.placeholder.dto.abstraction;


import it.almaviva.difesa.shared.common.dto.GenericResponseDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PlaceholderDTO implements GenericResponseDTO {

    private Long id;
    private String code;
    private String description;
    private String internalCode;
}
