package dev.jlcorradi.convention.core.service;

import dev.jlcorradi.convention.core.model.ConventionSession;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConventionSessionTest {

    @Test
    void isActive_returnsFalse_whenEndDatetimeIsNotNull() {
        ConventionSession conventionSession = new ConventionSession();
        conventionSession.setEndDatetime(LocalDateTime.now());
        assertFalse(conventionSession.isActive());
    }

    @Test
    void isActive_returnsTrue_whenStartDatetimeMinusDurationIsBeforeNow() {
        ConventionSession conventionSession = new ConventionSession();
        conventionSession.setStartDatetime(LocalDateTime.now().minusMinutes(20));
        conventionSession.setDurationMinutes(30);
        assertTrue(conventionSession.isActive());
    }

    @Test
    void isActive_returnsFalse_whenStartDatetimePlusDurationIsAfterNow() {
        ConventionSession conventionSession = new ConventionSession();
        conventionSession.setStartDatetime(LocalDateTime.now().minusMinutes(30));
        conventionSession.setDurationMinutes(20);
        assertFalse(conventionSession.isActive());
    }
}