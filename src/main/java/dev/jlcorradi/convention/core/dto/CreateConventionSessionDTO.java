package dev.jlcorradi.convention.core.dto;

import dev.jlcorradi.convention.core.model.Convention;
import lombok.Data;

@Data
public class CreateConventionSessionDTO {

    private Convention convention;
    private Integer durationInMinutes;

}
