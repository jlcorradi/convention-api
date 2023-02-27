package dev.jlcorradi.convention.core.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class RegisterVoterDTO {

    @NotEmpty(message = "ID is required")
    private String id;
    @NotEmpty(message = "Name is required")
    private String name;
    private String email;

}
