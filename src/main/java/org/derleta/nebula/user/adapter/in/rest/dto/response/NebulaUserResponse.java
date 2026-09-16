package org.derleta.nebula.user.adapter.in.rest.dto.response;

import lombok.*;
import org.derleta.nebula.game.domain.model.Game;
import org.derleta.nebula.gender.domain.model.Gender;
import org.derleta.nebula.nationality.domain.model.Nationality;
import org.derleta.nebula.user.domain.model.UserSettings;
import org.derleta.nebula.userachievement.domain.model.NebulaUserAchievement;
import org.springframework.hateoas.RepresentationModel;

import java.sql.Date;
import java.util.List;

/**
 * REST response DTO for a user (HATEOAS-aware).
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class NebulaUserResponse extends RepresentationModel<NebulaUserResponse> {
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
    private List<NebulaUserAchievement> achievements;
}
