package dev.jlcorradi.convention.core.service;

import dev.jlcorradi.convention.core.SessionClosedException;
import dev.jlcorradi.convention.core.dto.CreateConventionSessionDTO;
import dev.jlcorradi.convention.core.model.ConventionSession;
import dev.jlcorradi.convention.core.model.VotePoll;
import dev.jlcorradi.convention.core.repository.ConventionSessionRepository;
import dev.jlcorradi.convention.core.repository.VotePollRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class CachingConvertionService implements ConventionSessionService {

    private final ConventionSessionCache conventionSessionCache;
    private final ConventionSessionRepository conventionSessionRepository;
    private final VotePollRepository votePollRepository;

    @Override
    public ConventionSession startSession(CreateConventionSessionDTO createConventionSessionDTO) {
        ConventionSession conventionSession = ConventionSession.builder()
                .convention(createConventionSessionDTO.getConvention())
                .startDatetime(LocalDateTime.now())
                .durationMinutes(createConventionSessionDTO.getDurationInMinutes())
                .build();

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        executor.schedule(() -> {
            closeSession(conventionSession);
            executor.shutdown();
        }, conventionSession.getDurationMinutes(), TimeUnit.MINUTES);
        return null;
    }

    @Override
    public void closeSession(ConventionSession session) {
        log.trace("Closing session #{}", session.getId());
        ConventionSession activeSession = null;
        try {
            activeSession = conventionSessionCache.getActiveSession(session.getId());
        } catch (SessionClosedException e) {
            log.warn("Session has been closed by another thread.");
        }

        // We are manipulating the actual cache object to ensure that no more votes are placed.
        activeSession.setEndDatetime(LocalDateTime.now());

        // Summarizing votes to prevent overhead when reading poll results.
        List<VotePoll> votePolls = votePollRepository.findAllByConventionSession(activeSession);
        long proCount = votePolls.stream().filter(VotePoll::isVote).count();
        long conCount = votePolls.size() - proCount;
        activeSession.setVotesPro(proCount);
        activeSession.setVotesCon(conCount);

        conventionSessionRepository.save(activeSession);

        conventionSessionCache.destroySession(session.getId());

        // TODO: Publish results to sqs
        log.info("Publishing poll #{} results: {} - yes, {} - No.", session.getId(), session.getVotesPro(),
                session.getConvention());
        log.info("Session #{} closed successfully", session.getId());
    }

    /***
     * We check if there is any session that is supposed to be closed but are not due to system crash
     */
    @Override
    public void startupSessionsCheck() {
        log.trace("Executing Unclosed sessions Check.");
        List<ConventionSession> unclosedSessions = conventionSessionRepository.findAllByEndDatetimeNull();
        if (!unclosedSessions.isEmpty()) {
            log.warn("${} unclosed sessions found.", unclosedSessions.size());
        } else {
            log.info("No unclosed sessions found.");
        }

        for (ConventionSession session : unclosedSessions) {
            if (!session.isActive()) {
                log.warn("Unclosed session #{}. Will be closed.", session.getId());
                closeSession(session);
            }
        }
    }


}
