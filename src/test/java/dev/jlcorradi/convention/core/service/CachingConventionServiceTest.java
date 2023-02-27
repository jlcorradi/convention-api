package dev.jlcorradi.convention.core.service;

import dev.jlcorradi.convention.core.SessionClosedException;
import dev.jlcorradi.convention.core.dto.CreateConventionSessionDTO;
import dev.jlcorradi.convention.core.model.Convention;
import dev.jlcorradi.convention.core.model.ConventionSession;
import dev.jlcorradi.convention.core.repository.ConventionRepository;
import dev.jlcorradi.convention.core.repository.ConventionSessionRepository;
import dev.jlcorradi.convention.utils.DummyObjectProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
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

    @BeforeEach
    void setUp() {
        spySubject = spy(subject);
    }

    @Test
    void startSession_success() throws InterruptedException {
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
    void closeSession() throws SessionClosedException {
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
    void startupSessionsCheck() throws SessionClosedException {
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
}