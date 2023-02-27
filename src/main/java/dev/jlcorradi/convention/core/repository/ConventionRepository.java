package dev.jlcorradi.convention.core.repository;

import dev.jlcorradi.convention.core.model.Convention;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConventionRepository extends JpaRepository<Convention, Long> {
}
