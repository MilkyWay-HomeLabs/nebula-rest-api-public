package org.derleta.nebula.userachievement.application.port.in;

import org.springframework.data.domain.Page;
import org.derleta.nebula.userachievement.domain.model.UserAchievement;

/** Input port — fetches a filtered, paginated list of user achievements. */
public interface GetUserAchievementsPageUseCase {

    Page<UserAchievement> getUserAchievementsPage(UserAchievementPageQuery query);
}

