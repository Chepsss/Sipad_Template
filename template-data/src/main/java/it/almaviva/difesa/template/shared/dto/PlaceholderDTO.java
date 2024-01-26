package it.almaviva.difesa.template.shared.dto;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class PlaceholderDTO{

    private String placeHolder;

    @Override
    public String toString() {
        return "{" +
                "placeHolder='" + placeHolder + '\'' +
                '}';
    }

}
