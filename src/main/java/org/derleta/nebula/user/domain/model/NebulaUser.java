package org.derleta.nebula.user.domain.model;

import org.derleta.nebula.gender.domain.model.Gender;
import org.derleta.nebula.nationality.domain.model.Nationality;

import org.derleta.nebula.game.domain.model.Game;
import org.derleta.nebula.userachievement.domain.model.UserAchievement;
import java.sql.Date;
import java.util.List;

public record NebulaUser(long id, String login, String email, String firstName, String lastName,
                         int age, Date birthDate, Gender gender, Nationality nationality,
                         UserSettings settings, List<Game> games, List<UserAchievement> achievements) {
}
