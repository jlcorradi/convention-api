package dev.jlcorradi.convention.core.dto;

import dev.jlcorradi.convention.core.model.Convention;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CreateConventionSessionDTO {
    private Convention convention;
    private Integer durationInMinutes;

}
