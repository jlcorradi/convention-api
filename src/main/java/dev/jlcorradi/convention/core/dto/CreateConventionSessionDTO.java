package dev.jlcorradi.convention.core.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CreateConventionSessionDTO {
    private String convention;
    private Integer durationInMinutes;

}
