package com.safestep.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cctv_data")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CctvData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;

    // e.g. "ACTIVE", "INACTIVE"
    @NotBlank
    private String status;
}
