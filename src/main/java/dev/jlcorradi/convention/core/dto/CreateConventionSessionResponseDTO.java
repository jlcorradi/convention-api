package dev.jlcorradi.convention.core.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CreateConventionSessionResponseDTO {
    private Long id;
    private LocalDateTime startedDateTime;
}
