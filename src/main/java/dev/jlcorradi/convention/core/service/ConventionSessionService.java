package dev.jlcorradi.convention.core.service;

import dev.jlcorradi.convention.core.SessionClosedException;
import dev.jlcorradi.convention.core.dto.CreateConventionSessionDTO;
import dev.jlcorradi.convention.core.dto.CreateConventionSessionResponseDTO;
import dev.jlcorradi.convention.core.model.ConventionSession;

public interface ConventionSessionService {

    CreateConventionSessionResponseDTO startSession(CreateConventionSessionDTO createConventionSessionDTO);

    ConventionSession closeSession(ConventionSession session) throws SessionClosedException;

    void startupSessionsCheck() throws SessionClosedException;

}
