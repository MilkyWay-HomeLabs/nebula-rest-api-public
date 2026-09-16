package org.derleta.nebula.account.domain.model;

import org.derleta.nebula.account.domain.types.AccountResponseType;

public record AccountOperationResult(boolean success, AccountResponseType type) {
}
