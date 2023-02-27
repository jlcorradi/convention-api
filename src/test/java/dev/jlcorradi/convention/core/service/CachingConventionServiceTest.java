package dev.jlcorradi.convention.core.service;

import dev.jlcorradi.convention.core.DuplicatedVoteException;
import dev.jlcorradi.convention.core.SessionClosedException;
import dev.jlcorradi.convention.core.UnregisteredVoterException;
import dev.jlcorradi.convention.core.dto.CreateConventionSessionDTO;
import dev.jlcorradi.convention.core.dto.RegisterVoteDTO;
import dev.jlcorradi.convention.core.model.Convention;
import dev.jlcorradi.convention.core.model.ConventionSession;
import dev.jlcorradi.convention.core.model.Voter;
import dev.jlcorradi.convention.core.repository.ConventionRepository;
import dev.jlcorradi.convention.core.repository.ConventionSessionRepository;
import dev.jlcorradi.convention.core.repository.VoterRepository;
import dev.jlcorradi.convention.utils.DummyObjectProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

import static dev.jlcorradi.convention.utils.DummyObjectProvider.voter;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class CachingConventionServiceTest {

    @Autowired
    DefaultConventionService subject;

    DefaultConventionService spySubject;

    @Autowired
    ConventionRepository conventionRepository;

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
        Convention convention = conventionRepository.save(DummyObjectProvider.convention());
        CreateConventionSessionDTO createConventionSessionDTO = CreateConventionSessionDTO.builder()
                .convention(convention)
                .durationInMinutes(1)
                .build();

        // WHEN
        ConventionSession conventionSession = subject.startSession(createConventionSessionDTO);

        // THEN
        Assertions.assertTrue(conventionSession.isActive());

    }

    @Test
    void closeSession_success() throws SessionClosedException {
        // GIVEN
        Convention convention = conventionRepository.save(DummyObjectProvider.convention());
        CreateConventionSessionDTO createConventionSessionDTO = CreateConventionSessionDTO.builder()
                .convention(convention)
                .durationInMinutes(60)
                .build();
        ConventionSession conventionSession = subject.startSession(createConventionSessionDTO);

        // WHEN
        ConventionSession closedSession = subject.closeSession(conventionSession);

        // THEN
        assertFalse(closedSession.isActive());
    }

    @Test
    void startupSessionsCheck_closesSessions() throws SessionClosedException {
        // GIVEN
        Convention convention = conventionRepository.save(DummyObjectProvider.convention());
        ConventionSession session = ConventionSession.builder()
                .convention(convention)
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
        Convention convention = conventionRepository.save(DummyObjectProvider.convention());
        ConventionSession session = spySubject.startSession(CreateConventionSessionDTO.builder()
                .convention(convention)
                .durationInMinutes(1)
                .build());
        Voter voter = voterRepository.save(voter());

        votePollService.registerVote(RegisterVoteDTO.builder()
                .conventionSessionId(session.getId())
                .voterId(voter.getId())
                .vote(true)
                .build());

        // WHEN
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
        Convention convention = conventionRepository.save(DummyObjectProvider.convention());
        ConventionSession session = subject.startSession(CreateConventionSessionDTO.builder()
                .convention(convention)
                .durationInMinutes(1)
                .build());
        Voter voter = voterRepository.save(voter());

        subject.closeSession(session);

        assertThrows(SessionClosedException.class, () -> votePollService.registerVote(RegisterVoteDTO.builder()
                .conventionSessionId(session.getId())
                .voterId(voter.getId())
                .vote(true)
                .build()));

    }

    @Test
    void duplicatedVote_throwException() throws UnregisteredVoterException,
            SessionClosedException, DuplicatedVoteException {
        // GIVEN
        Convention convention = conventionRepository.save(DummyObjectProvider.convention());
        ConventionSession session = subject.startSession(CreateConventionSessionDTO.builder()
                .convention(convention)
                .durationInMinutes(1)
                .build());

        Voter voter = voterRepository.save(voter());
        RegisterVoteDTO theVote = RegisterVoteDTO.builder()
                .conventionSessionId(session.getId())
                .voterId(voter.getId())
                .vote(true)
                .build();

        votePollService.registerVote(theVote);

        assertThrows(DuplicatedVoteException.class, () -> votePollService.registerVote(theVote));

        subject.closeSession(session);

        ConventionSession closedSession = conventionSessionRepository.findById(session.getId())
                .orElse(session);
        assertFalse(closedSession.isActive());
        assertEquals(1, closedSession.getVotesPro());
        assertEquals(0, closedSession.getVotesCon());

    }
}