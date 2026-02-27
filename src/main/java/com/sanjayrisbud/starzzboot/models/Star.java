package com.sanjayrisbud.starzzboot.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "stars")
public class Star {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "star_id")
    private Integer id;

    @Column(name = "star_name")
    private String name;

    @Column(name = "star_type")
    private String type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "constellation_id")
    private Constellation constellation;

    @Column(name = "right_ascension")
    private Integer rightAscension;

    @Column(name = "declination")
    private Integer declination;

    @Column(name = "apparent_magnitude")
    private Integer apparentMagnitude;

    @Column(name = "spectral_type")
    private String spectralType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "added_by")
    private User addedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verified_by")
    private User verifiedBy;
}
