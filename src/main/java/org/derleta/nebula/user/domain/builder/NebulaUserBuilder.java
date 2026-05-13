package org.derleta.nebula.user.domain.builder;

import org.derleta.nebula.user.domain.model.NebulaUser;
import org.derleta.nebula.user.domain.model.UserSettings;
import org.derleta.nebula.game.domain.model.Game;
import org.derleta.nebula.userachievement.domain.model.UserAchievement;
import org.derleta.nebula.gender.domain.model.Gender;
import org.derleta.nebula.nationality.domain.model.Nationality;

import java.sql.Date;
import java.util.List;

public interface NebulaUserBuilder {

    NebulaUser build();

    NebulaUserBuilder id(long id);

    NebulaUserBuilder login(String login);

    NebulaUserBuilder email(String email);

    NebulaUserBuilder firstName(String firstName);

    NebulaUserBuilder lastName(String lastName);

    NebulaUserBuilder age(int age);

    NebulaUserBuilder birthDate(Date birthDate);

    NebulaUserBuilder gender(Gender gender);

    NebulaUserBuilder nationality(Nationality nationality);

    NebulaUserBuilder settings(UserSettings settings);

    NebulaUserBuilder games(List<Game> games);

    NebulaUserBuilder achievements(List<UserAchievement> achievements);

}
