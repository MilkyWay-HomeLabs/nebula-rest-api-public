package org.derleta.nebula.account.application.port.out;

import org.derleta.nebula.account.domain.model.Account;

import java.util.Optional;

/**
 * Output port — abstraction over the account persistence layer.
 */
public interface AccountRepositoryPort {

    /**
     * Persists a new account and returns the saved instance.
     */
    Account save(Account account);

    /**
     * Finds an account by its unique identifier.
     */
    Optional<Account> findById(long id);
}
