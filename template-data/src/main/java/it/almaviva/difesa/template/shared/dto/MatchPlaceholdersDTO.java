package it.almaviva.difesa.template.shared.dto;

import lombok.Data;

@Data
public class MatchPlaceholdersDTO {

    /* true (default) if placeholders match with template ones */
    private Boolean placeholdersDoMatch = true;

    /* Template placeholder names,
     empty if no placeholders are present in a Template */
    private String nomiPlaceholders = "";

}
