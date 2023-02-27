package dev.jlcorradi.convention.utils;

import dev.jlcorradi.convention.core.model.Convention;
import dev.jlcorradi.convention.core.model.ConventionSession;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DummyObjectProvider {

    public static final String DUMMY_CONVENTION = "Dummy Convention";

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

}
