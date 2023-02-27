package dev.jlcorradi.convention.core.repository;

import dev.jlcorradi.convention.core.model.ConventionSession;
import dev.jlcorradi.convention.core.model.VotePoll;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VotePollRepository extends JpaRepository<VotePoll, Long> {
    List<VotePoll> findAllByConventionSession(ConventionSession session);
}
