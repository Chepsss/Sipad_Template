package it.almaviva.difesa.template.shared.entity.shared.composite;

import lombok.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TemplatePlaceholderCompositeKey implements Serializable {

    private Long templateId;
    private Long placeholderId;
}
