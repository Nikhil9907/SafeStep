package com.safestep.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "crime_data")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CrimeData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String title;

    private String description;

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;

    // e.g. "HIGH", "MEDIUM", "LOW"
    @NotBlank
    private String severity;

    private LocalDateTime reportedAt;

    @PrePersist
    protected void onCreate() {
        if (this.reportedAt == null) {
            this.reportedAt = LocalDateTime.now();
        }
    }
}
