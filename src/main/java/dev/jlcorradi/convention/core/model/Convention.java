package dev.jlcorradi.convention.core.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "convention")
public class Convention {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "description", nullable = false)
    private String description;
}
