package it.almaviva.difesa.template.templateModel.dto.abstraction;


import com.fasterxml.jackson.annotation.JsonFormat;
import it.almaviva.difesa.shared.common.dto.GenericRequestDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;


@Getter
@Setter
@NoArgsConstructor
public class TemplateRequestDTO implements GenericRequestDTO {

    @NotNull
    private String name;

    @NotNull
    private String format;

    @NotNull
    @Positive
    @Min(1)
    private Long templateTypeId;

    @NotNull
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate validityStartDate;

    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate validityEndDate;

    @NotNull
    private String content;



}
