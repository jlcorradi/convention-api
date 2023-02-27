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
    @NotEmpty(message = "Voter ID is required")
    private String voterId;

    @NotEmpty(message = "Convention ID is required")
    private Long conventionSessionId;

    @NotEmpty(message = "Vote is required")
    private Boolean vote;
}
