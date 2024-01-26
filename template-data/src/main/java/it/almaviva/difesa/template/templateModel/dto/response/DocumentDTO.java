package it.almaviva.difesa.template.templateModel.dto.response;

import it.almaviva.difesa.shared.common.dto.GenericResponseDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
public class DocumentDTO implements GenericResponseDTO {

    /**
     * file content in base64 format
     */
    private String file;
    private ArrayList<String> placeholderErrors;
    private Boolean error;
}
