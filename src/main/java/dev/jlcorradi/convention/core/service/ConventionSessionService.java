package dev.jlcorradi.convention.core.service;

import dev.jlcorradi.convention.core.dto.CreateConventionSessionDTO;
import dev.jlcorradi.convention.core.model.ConventionSession;

public interface ConventionSessionService {

    ConventionSession startSession(CreateConventionSessionDTO createConventionSessionDTO);

    void closeSession(ConventionSession session);

    void startupSessionsCheck();

}
