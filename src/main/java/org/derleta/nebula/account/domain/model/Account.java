package org.derleta.nebula.account.domain.model;

import java.util.Date;

public record Account(long id, String email, String login, Date birthDate, int nationalityId, int genderId) {
}
