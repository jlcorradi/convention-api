package dev.jlcorradi.convention.core.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CreateConventionSessionDTO {
    @NotEmpty(message = "Convention is required")
    private String convention;
    @NotNull(message = "Duration is required")
    private Integer durationInMinutes;

}
