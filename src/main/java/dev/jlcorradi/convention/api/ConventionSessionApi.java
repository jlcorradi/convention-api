package dev.jlcorradi.convention.api;

import dev.jlcorradi.convention.core.DuplicatedVoteException;
import dev.jlcorradi.convention.core.SessionClosedException;
import dev.jlcorradi.convention.core.UnregisteredVoterException;
import dev.jlcorradi.convention.core.dto.CreateConventionSessionDTO;
import dev.jlcorradi.convention.core.dto.CreateConventionSessionResponseDTO;
import dev.jlcorradi.convention.core.dto.RegisterVoteDTO;
import dev.jlcorradi.convention.core.service.ConventionSessionService;
import dev.jlcorradi.convention.core.service.VotePollService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Convention Session API", description = "Endpoints for managing convention sessions")
public class ConventionSessionApi {

    private final ConventionSessionService conventionSessionService;
    private final VotePollService votePollService;

    @Operation(summary = "Start a new convention session",
            description = "Create a new convention session with the specified configuration.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Convention session created",
                    content = @Content(schema = @Schema(implementation = CreateConventionSessionResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input")})
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

    @Operation(summary = "Register a vote in a convention session",
            description = "Register a vote with the specified choice for the convention session with the specified ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vote registered"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Convention session not found"),
            @ApiResponse(responseCode = "409", description = "Duplicate vote"),
            @ApiResponse(responseCode = "410", description = "Session closed")})
    @PostMapping("/{id}/vote")
    @ResponseStatus(HttpStatus.OK)
    public void registerVote(
            @Parameter(description = "ID of the convention session", required = true)
            @PathVariable("id") Long sessionId,
            @Parameter(description = "ID of the voter", required = true)
            @RequestHeader("voterId") String voterId,
            @RequestBody RegisterVoteDTO registerVoteDTO)
            throws DuplicatedVoteException, UnregisteredVoterException, SessionClosedException {
        votePollService.registerVote(sessionId, voterId, registerVoteDTO);
    }
}
