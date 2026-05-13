package org.derleta.nebula.userachievement.application.port.in;

import org.derleta.nebula.userachievement.domain.model.UserAchievement;

import java.util.List;

/** Input port — fetches all achievements for a given user. */
public interface GetUserAchievementsListUseCase {

    List<UserAchievement> getUserAchievementsList(long userId);
}

