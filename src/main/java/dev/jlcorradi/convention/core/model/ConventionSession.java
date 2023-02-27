package dev.jlcorradi.convention.core.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Builder
@Data
@Entity
@Table(name = "convention_session")
public class ConventionSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "convention_id", nullable = false)
    private Convention convention;

    @Column(name = "duration_minutes", nullable = false)
    private int durationMinutes = 1;

    @Column(name = "start_datetime", nullable = false)
    private LocalDateTime startDatetime;

    @Column(name = "end_datetime")
    private LocalDateTime endDatetime;

    @Column(name = "votes_pro")
    private Long votesPro;

    @Column(name = "votes_con")
    private Long votesCon;

    public boolean isActive() {
        if (null != this.endDatetime) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        return this.startDatetime.plus(this.durationMinutes, ChronoUnit.MINUTES).isBefore(now);
    }
}
