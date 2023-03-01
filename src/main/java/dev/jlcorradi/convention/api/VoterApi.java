package dev.jlcorradi.convention.api;

import dev.jlcorradi.convention.core.VoterIdDuplicationException;
import dev.jlcorradi.convention.core.dto.RegisterVoterDTO;
import dev.jlcorradi.convention.core.service.VoterService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Voter API", description = "Manage the users that are allowed to vote")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/voter")
public class VoterApi {

    private final VoterService voterService;

    @PostMapping
    public ResponseEntity<Void> registerVoter(@Validated @RequestBody RegisterVoterDTO registerVoterDTO)
            throws VoterIdDuplicationException {
        voterService.registerVoter(registerVoterDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
