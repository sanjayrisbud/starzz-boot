package com.sanjayrisbud.starzzboot.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "galaxies")
public class Galaxy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "galaxy_id")
    private Integer id;

    @Column(name = "galaxy_name")
    private String name;

    @Column(name = "galaxy_type")
    private Integer type;

    @Column(name = "distance_mly")
    private Integer distanceMly;

    @Column(name = "redshift")
    private Integer redshift;

    @Column(name = "mass_solar")
    private Integer massSolar;

    @Column(name = "diameter_ly")
    private Integer diameterLy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "added_by")
    private User addedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verified_by")
    private User verifiedBy;

    @OneToMany(targetEntity = Constellation.class, mappedBy = "galaxy")
    private Set<Constellation> constellations = new HashSet<>();
}
