package dev.jlcorradi.convention.core.service;

import dev.jlcorradi.convention.core.SessionClosedException;
import dev.jlcorradi.convention.core.model.ConventionSession;
import dev.jlcorradi.convention.core.repository.ConventionSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class ConventionSessionCache {

    public static final String MSG_ADDING_CACHE = "Adding session #{} to cache.";
    public static final String MSG_DESTROYING_CACHE = "Destroying session #{}.";

    private final ConventionSessionRepository conventionSessionRepository;

    private final Map<Long, ConventionSession> cacheObject = new HashMap<>();

    public ConventionSession getActiveSession(Long sessionId) throws SessionClosedException {
        ConventionSession activeSession = cacheObject.get(sessionId);
        if (null == activeSession) {
            activeSession = conventionSessionRepository.findById(sessionId)
                    .filter(ConventionSession::isActive)
                    .orElseThrow(SessionClosedException::new);

            log.trace(MSG_ADDING_CACHE, sessionId);
            cacheObject.put(sessionId, activeSession);
        }

        return activeSession;
    }

    public void destroySession(Long sessionId) {
        log.trace(MSG_DESTROYING_CACHE, sessionId);
        cacheObject.remove(sessionId);
    }

}
