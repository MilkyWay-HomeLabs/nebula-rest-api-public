package org.derleta.nebula.userachievement.domain.exception;

/** Thrown when a requested user achievement does not exist. */
public class UserAchievementNotFoundException extends RuntimeException {

    public UserAchievementNotFoundException(String message) {
        super(message);
    }
}

