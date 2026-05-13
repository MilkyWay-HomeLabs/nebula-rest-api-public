package org.derleta.nebula.user.domain.builder.impl;

import org.derleta.nebula.game.domain.model.Game;
import org.derleta.nebula.gender.domain.model.Gender;
import org.derleta.nebula.nationality.domain.model.Nationality;
import org.derleta.nebula.user.domain.builder.NebulaUserBuilder;
import org.derleta.nebula.user.domain.model.NebulaUser;
import org.derleta.nebula.user.domain.model.UserSettings;
import org.derleta.nebula.userachievement.domain.model.UserAchievement;

import java.sql.Date;
import java.util.List;

public final class NebulaUserBuilderImpl implements NebulaUserBuilder {

    private long id;
    private String login;
    private String email;
    private String firstName;
    private String lastName;
    private int age;
    private Date birthDate;
    private Gender gender;
    private Nationality nationality;
    private UserSettings settings;
    private List<Game> games;
    private List<UserAchievement> achievements;

    @Override
    public NebulaUser build() {
        return new NebulaUser(id, login, email, firstName, lastName,
                age, birthDate, gender, nationality, settings, games, achievements);
    }

    @Override
    public NebulaUserBuilder id(long id) {
        this.id = id;
        return this;
    }

    @Override
    public NebulaUserBuilder login(String login) {
        this.login = login;
        return this;
    }

    @Override
    public NebulaUserBuilder email(String email) {
        this.email = email;
        return this;
    }

    @Override
    public NebulaUserBuilder firstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    @Override
    public NebulaUserBuilder lastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    @Override
    public NebulaUserBuilder age(int age) {
        this.age = age;
        return this;
    }

    @Override
    public NebulaUserBuilder birthDate(Date birthDate) {
        this.birthDate = birthDate;
        return this;
    }

    @Override
    public NebulaUserBuilder gender(Gender gender) {
        this.gender = gender;
        return this;
    }

    @Override
    public NebulaUserBuilder nationality(Nationality nationality) {
        this.nationality = nationality;
        return this;
    }

    @Override
    public NebulaUserBuilder settings(UserSettings settings) {
        this.settings = settings;
        return this;
    }

    @Override
    public NebulaUserBuilder games(List<Game> games) {
        this.games = games;
        return this;
    }

    @Override
    public NebulaUserBuilder achievements(List<UserAchievement> achievements) {
        this.achievements = achievements;
        return this;
    }

}
