package org.derleta.nebula.user.application.port.in.command;
import java.sql.Date;
/** Command carrying the data needed to update a user profile. */
public record UpdateProfileCommand(
        long userId,
        String firstName,
        String lastName,
        Date birthdate,
        int nationalityId,
        int genderId) {
}
