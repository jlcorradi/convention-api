package dev.jlcorradi.convention.core.repository;

import dev.jlcorradi.convention.core.model.Voter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoterRepository extends JpaRepository<Voter, String> {
}
