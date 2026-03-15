package com.sanjayrisbud.starzzboot.helpers;

import com.sanjayrisbud.starzzboot.models.Constellation;
import com.sanjayrisbud.starzzboot.models.Galaxy;
import com.sanjayrisbud.starzzboot.models.Star;
import com.sanjayrisbud.starzzboot.models.User;

public class EntityFactory {
    public static User buildUser() {
        return buildUser(1, "Test User");
    }

    public static User buildUser(Integer id, String name) {
        return User.builder().id(id).name(name).build();
    }

    public static Galaxy buildGalaxy() {
        return buildGalaxy(1, "Test Galaxy");
    }

    public static Galaxy buildGalaxy(Integer id, String name) {
        return buildGalaxy(id, name, buildUser(), buildUser());
    }

    public static Galaxy buildGalaxy(Integer id, String name, User addedBy, User verifiedBy) {
        return Galaxy.builder().id(id).name(name)
                .addedBy(addedBy).verifiedBy(verifiedBy).build();
    }

    public static Constellation buildConstellation() {
        return buildConstellation(1, "Test Constellation");
    }

    public static Constellation buildConstellation(Integer id, String name) {
        return buildConstellation(id, name, buildGalaxy(), buildUser(), buildUser());
    }

    public static Constellation buildConstellation(
            Integer id, String name, Galaxy galaxy, User addedBy, User verifiedBy) {
        return Constellation.builder().id(id).name(name).galaxy(galaxy)
                .addedBy(addedBy).verifiedBy(verifiedBy).build();
    }

    public static Star buildStar() {
        return buildStar(1, "Test Star");
    }

    public static Star buildStar(Integer id, String name) {
        return buildStar(id, name, buildConstellation(), buildUser(), buildUser());
    }

    public static Star buildStar(Integer id, String name, Constellation constellation,
                                 User addedBy, User verifiedBy) {
        return Star.builder().id(id).name(name).constellation(constellation)
                .addedBy(addedBy).verifiedBy(verifiedBy).build();
    }
}
