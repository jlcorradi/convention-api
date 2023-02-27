package dev.jlcorradi.convention.utils;

import dev.jlcorradi.convention.core.model.Convention;
import dev.jlcorradi.convention.core.model.ConventionSession;
import dev.jlcorradi.convention.core.model.Voter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DummyObjectProvider {

    public static final String DUMMY_CONVENTION = "Dummy Convention";
    public static final String DUMMY_EMAIL_COM = "dummy@email.com";
    public static final String DUMMY_ID = "00000000000";
    public static final String DUMMY_NAME = "Dummy Name";

    public static Convention convention() {
        return Convention.builder()
                .description(DUMMY_CONVENTION)
                .build();
    }

    public static ConventionSession conventionSession(Convention convention, Integer durationInMinutes) {
        return ConventionSession.builder()
                .convention(convention)
                .durationMinutes(durationInMinutes)
                .build();
    }

    public static ConventionSession conventionSession(Integer durationInMinutes) {
        return conventionSession(convention(), durationInMinutes);
    }

    public static Voter voter() {
        return Voter.builder()
                .email(DUMMY_EMAIL_COM)
                .name(DUMMY_NAME)
                .id(DUMMY_ID)
                .build();
    }

}
