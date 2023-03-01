package dev.jlcorradi.convention.api;

import dev.jlcorradi.convention.core.DuplicatedVoteException;
import dev.jlcorradi.convention.core.SessionClosedException;
import dev.jlcorradi.convention.core.UnregisteredVoterException;
import dev.jlcorradi.convention.core.dto.CreateConventionSessionDTO;
import dev.jlcorradi.convention.core.dto.CreateConventionSessionResponseDTO;
import dev.jlcorradi.convention.core.dto.RegisterVoteDTO;
import dev.jlcorradi.convention.core.service.ConventionSessionService;
import dev.jlcorradi.convention.core.service.VotePollService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/convention-session")
public class ConventionSessionApi {

    private final ConventionSessionService conventionSessionService;
    private final VotePollService votePollService;

    @PostMapping
    public ResponseEntity<CreateConventionSessionResponseDTO> createConventionSession(
            @Valid @RequestBody CreateConventionSessionDTO createConventionSessionDTO) {
        CreateConventionSessionResponseDTO createConventionSessionResponseDTO =
                conventionSessionService.startSession(createConventionSessionDTO);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createConventionSessionResponseDTO.getId())
                .toUri();

        return ResponseEntity.created(location)
                .body(createConventionSessionResponseDTO);
    }

    @PostMapping("/{id}/vote")
    @ResponseStatus(HttpStatus.OK)
    public void registerVote(@PathVariable("id") Long sessionId, @RequestHeader("voterId") String voterId,
                             @RequestBody RegisterVoteDTO registerVoteDTO)
            throws DuplicatedVoteException, UnregisteredVoterException, SessionClosedException {
        votePollService.registerVote(sessionId, voterId, registerVoteDTO);
    }

}
