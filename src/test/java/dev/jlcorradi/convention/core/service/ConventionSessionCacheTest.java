package dev.jlcorradi.convention.core.service;

import dev.jlcorradi.convention.core.SessionClosedException;
import dev.jlcorradi.convention.core.model.ConventionSession;
import dev.jlcorradi.convention.core.repository.ConventionSessionRepository;
import dev.jlcorradi.convention.utils.DummyObjectProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConventionSessionCacheTest {

    @InjectMocks
    ConventionSessionCache subject;

    @Mock
    ConventionSessionRepository conventionSessionRepository;

    @Test
    void getActiveSession_hasActiveSession() throws SessionClosedException {
        // GIVEN
        ConventionSession conventionSession = DummyObjectProvider.conventionSession(1);
        conventionSession.setStartDatetime(LocalDateTime.now());

        when(conventionSessionRepository.findById(anyLong())).thenReturn(Optional.of(conventionSession));

        // WHEN
        ConventionSession activeSession = subject.getActiveSession(1L);

        // THEN
        assertTrue(activeSession.isActive());
    }

    @Test
    void getActiveSession_hasNoSession() throws SessionClosedException {
        // GIVEN
        ConventionSession conventionSession = DummyObjectProvider.conventionSession(1);
        conventionSession.setStartDatetime(LocalDateTime.now().minusMinutes(2));

        when(conventionSessionRepository.findById(anyLong())).thenReturn(Optional.of(conventionSession));

        // WHEN - THEN
        assertThrows(SessionClosedException.class, () -> subject.getActiveSession(1L));

    }

}