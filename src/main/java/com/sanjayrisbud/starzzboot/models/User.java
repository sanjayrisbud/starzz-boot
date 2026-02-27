package com.sanjayrisbud.starzzboot.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer id;

    @Column(name = "username")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "date_of_birth")
    private String dateOfBirth;

    @OneToMany(targetEntity = Galaxy.class, mappedBy = "addedBy")
    private Set<Galaxy> galaxiesAdded = new HashSet<>();

    @OneToMany(targetEntity = Galaxy.class, mappedBy = "verifiedBy")
    private Set<Galaxy> galaxiesVerified = new HashSet<>();

    @OneToMany(targetEntity = Constellation.class, mappedBy = "addedBy")
    private Set<Constellation> constellationsAdded = new HashSet<>();

    @OneToMany(targetEntity = Constellation.class, mappedBy = "verifiedBy")
    private Set<Constellation> constellationsVerified = new HashSet<>();

    @OneToMany(targetEntity = Star.class, mappedBy = "addedBy")
    private Set<Star> starsAdded = new HashSet<>();

    @OneToMany(targetEntity = Star.class, mappedBy = "verifiedBy")
    private Set<Star> starsVerified = new HashSet<>();
}
