package dev.jlcorradi.convention.core.repository;

import dev.jlcorradi.convention.core.model.ConventionSession;
import dev.jlcorradi.convention.core.model.VotePoll;
import dev.jlcorradi.convention.core.model.Voter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VotePollRepository extends JpaRepository<VotePoll, Long> {
    List<VotePoll> findAllByConventionSession(ConventionSession session);

    Optional<VotePoll> findByVoterAndConventionSession(Voter voter, ConventionSession activeSession);
}
