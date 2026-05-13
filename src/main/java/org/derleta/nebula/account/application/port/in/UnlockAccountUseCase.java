package org.derleta.nebula.account.application.port.in;

import org.derleta.nebula.account.domain.model.AccountOperationResult;

public interface UnlockAccountUseCase {

    AccountOperationResult unlock(long accountId);

}
