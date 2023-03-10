package dev.jlcorradi.convention.core.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "voter")
public class Voter {

    @Id
    @Column(name = "id", nullable = false, length = 20)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email")
    private String email;
}
