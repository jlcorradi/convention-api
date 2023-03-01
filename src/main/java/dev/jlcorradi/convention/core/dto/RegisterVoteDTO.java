package dev.jlcorradi.convention.core.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterVoteDTO {
    @NotEmpty(message = "Vote is required")
    private Boolean vote;
}
