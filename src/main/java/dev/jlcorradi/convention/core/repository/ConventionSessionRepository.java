package dev.jlcorradi.convention.core.repository;

import dev.jlcorradi.convention.core.model.ConventionSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConventionSessionRepository extends JpaRepository<ConventionSession, Long> {

    List<ConventionSession> findAllByEndDatetimeNull();

}
