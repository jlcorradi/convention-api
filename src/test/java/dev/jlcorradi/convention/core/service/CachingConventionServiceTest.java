package dev.jlcorradi.convention.core.service;

import dev.jlcorradi.convention.core.DuplicatedVoteException;
import dev.jlcorradi.convention.core.SessionClosedException;
import dev.jlcorradi.convention.core.UnregisteredVoterException;
import dev.jlcorradi.convention.core.dto.CreateConventionSessionDTO;
import dev.jlcorradi.convention.core.dto.CreateConventionSessionResponseDTO;
import dev.jlcorradi.convention.core.dto.RegisterVoteDTO;
import dev.jlcorradi.convention.core.model.ConventionSession;
import dev.jlcorradi.convention.core.model.Voter;
import dev.jlcorradi.convention.core.repository.ConventionSessionRepository;
import dev.jlcorradi.convention.core.repository.VoterRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

import static dev.jlcorradi.convention.utils.DummyObjectProvider.DUMMY_CONVENTION;
import static dev.jlcorradi.convention.utils.DummyObjectProvider.voter;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest
class CachingConventionServiceTest {

    @Autowired
    DefaultConventionService subject;

    DefaultConventionService spySubject;

    @Autowired
    ConventionSessionRepository conventionSessionRepository;

    @Autowired
    DefaultVotePollService votePollService;

    @Autowired
    VoterRepository voterRepository;

    @BeforeEach
    void setUp() {
        spySubject = spy(subject);
    }

    @Test
    void startSession_success() {
        // GIVEN
        CreateConventionSessionDTO createConventionSessionDTO = CreateConventionSessionDTO.builder()
                .convention(DUMMY_CONVENTION)
                .durationInMinutes(1)
                .build();
        // WHEN
        ConventionSession session = createSession(subject, createConventionSessionDTO);

        // THEN
        Assertions.assertTrue(session.isActive());

    }

    @Test
    void closeSession_success() throws SessionClosedException {
        // GIVEN
        CreateConventionSessionDTO createConventionSessionDTO = CreateConventionSessionDTO.builder()
                .convention(DUMMY_CONVENTION)
                .durationInMinutes(1)
                .build();

        ConventionSession conventionSession = createSession(subject, createConventionSessionDTO);

        // WHEN
        ConventionSession closedSession = subject.closeSession(conventionSession);

        // THEN
        assertFalse(closedSession.isActive());
    }

    @Test
    void startupSessionsCheck_closesSessions() throws SessionClosedException {
        // GIVEN
        CreateConventionSessionDTO createConventionSessionDTO = CreateConventionSessionDTO.builder()
                .convention(DUMMY_CONVENTION)
                .durationInMinutes(1)
                .build();

        ConventionSession session = ConventionSession.builder()
                .description(DUMMY_CONVENTION)
                .durationMinutes(5)
                .startDatetime(LocalDateTime.now().minusMinutes(10))
                .build();

        conventionSessionRepository.save(session);

        // WHEN
        spySubject.startupSessionsCheck();

        // THEN
        verify(spySubject, times(1)).closeSession(any());
    }

    @Test
    void fullVotingSession_success() throws UnregisteredVoterException, SessionClosedException,
            InterruptedException, DuplicatedVoteException {
        // GIVEN
        CreateConventionSessionDTO createConventionSessionDTO = CreateConventionSessionDTO.builder()
                .convention(DUMMY_CONVENTION)
                .durationInMinutes(1)
                .build();

        ConventionSession session = createSession(spySubject, createConventionSessionDTO);
        Voter voter = voterRepository.save(voter());

        votePollService.registerVote(session.getId(), RegisterVoteDTO.builder()
                .voterId(voter.getId())
                .vote(true)
                .build());

        // WHEN
        log.info("Waiting for the session to end...");
        Thread.sleep(2000 * 60);

        // THEN
        verify(spySubject).closeSession(any());
        ConventionSession closedSession = conventionSessionRepository.findById(session.getId())
                .orElse(session);
        assertFalse(closedSession.isActive());
        assertEquals(1, closedSession.getVotesPro());
        assertEquals(0, closedSession.getVotesCon());

    }

    @Test
    void voteAfterSessionClosed_throwsException() throws UnregisteredVoterException, InterruptedException, SessionClosedException {
        // GIVEN
        CreateConventionSessionDTO createConventionSessionDTO = CreateConventionSessionDTO.builder()
                .convention(DUMMY_CONVENTION)
                .durationInMinutes(1)
                .build();

        ConventionSession session = createSession(subject, createConventionSessionDTO);
        Voter voter = voterRepository.save(voter());

        subject.closeSession(session);

        assertThrows(SessionClosedException.class, () -> votePollService.registerVote(
                session.getId(), RegisterVoteDTO.builder()
                        .voterId(voter.getId())
                        .vote(true)
                        .build())
        );

    }

    @Test
    void duplicatedVote_throwException() throws UnregisteredVoterException,
            SessionClosedException, DuplicatedVoteException {
        // GIVEN
        CreateConventionSessionDTO createConventionSessionDTO = CreateConventionSessionDTO.builder()
                .convention(DUMMY_CONVENTION)
                .durationInMinutes(1)
                .build();

        ConventionSession session = createSession(subject, createConventionSessionDTO);

        Voter voter = voterRepository.save(voter());
        RegisterVoteDTO theVote = RegisterVoteDTO.builder()
                .voterId(voter.getId())
                .vote(true)
                .build();

        votePollService.registerVote(session.getId(), theVote);

        assertThrows(DuplicatedVoteException.class, () -> votePollService.registerVote(session.getId(), theVote));

        subject.closeSession(session);

        ConventionSession closedSession = conventionSessionRepository.findById(session.getId())
                .orElse(session);
        assertFalse(closedSession.isActive());
        assertEquals(1, closedSession.getVotesPro());
        assertEquals(0, closedSession.getVotesCon());

    }

    private ConventionSession createSession(ConventionSessionService service,
                                            CreateConventionSessionDTO createConventionSessionDTO) {
        CreateConventionSessionResponseDTO createConventionSessionResponseDTO = service.startSession(createConventionSessionDTO);
        return conventionSessionRepository.findById(createConventionSessionResponseDTO.getId())
                .orElse(null);
    }
}