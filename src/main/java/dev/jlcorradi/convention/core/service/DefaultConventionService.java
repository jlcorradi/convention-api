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
public class DefaultConventionService implements ConventionSessionService {

    public static final String MSG_RESULTS_YES_NO = "Publishing poll #{} results: {} - yes, {} - No.";
    public static final String MSG_SESSION_CLOSED_SUCCESSFULLY = "Session #{} closed successfully";
    public static final String MSG_UNCLOSED_SESSIONS_FOUND = "${} unclosed sessions found.";
    public static final String MSG_NO_UNCLOSED_SESSIONS_FOUND = "No unclosed sessions found.";

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
            try {
                closeSession(conventionSession);
            } catch (SessionClosedException e) {
                log.warn("The Session #{} had already been closed.", conventionSession.getId());
            }
            executor.shutdown();
        }, conventionSession.getDurationMinutes(), TimeUnit.MINUTES);

        return conventionSessionRepository.save(conventionSession);
    }

    @Override
    public ConventionSession closeSession(ConventionSession session) throws SessionClosedException {
        log.trace("Closing session #{}", session.getId());
        ConventionSession activeSession = conventionSessionRepository.findById(session.getId())
                .orElseThrow(SessionClosedException::new);

        conventionSessionCache.destroySession(activeSession.getId());

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
        log.info(MSG_RESULTS_YES_NO, session.getId(), session.getVotesPro(),
                session.getConvention());
        log.info(MSG_SESSION_CLOSED_SUCCESSFULLY, session.getId());

        return activeSession;
    }

    /***
     * We check if there is any session that is supposed to be closed but are not due to system crash
     */
    @Override
    public void startupSessionsCheck() throws SessionClosedException {
        log.trace("Executing Unclosed sessions Check.");
        List<ConventionSession> unclosedSessions = conventionSessionRepository.findAllByEndDatetimeNull();
        if (!unclosedSessions.isEmpty()) {
            log.warn(MSG_UNCLOSED_SESSIONS_FOUND, unclosedSessions.size());
        } else {
            log.info(MSG_NO_UNCLOSED_SESSIONS_FOUND);
        }

        for (ConventionSession session : unclosedSessions) {
            if (!session.isActive()) {
                log.warn("Unclosed session #{}. Will be closed.", session.getId());
                closeSession(session);
            }
        }
    }


}
