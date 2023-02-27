package dev.jlcorradi.convention.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterVoteDTO {
    private String voterId;
    private Long conventionSessionId;
    private boolean vote;
}
