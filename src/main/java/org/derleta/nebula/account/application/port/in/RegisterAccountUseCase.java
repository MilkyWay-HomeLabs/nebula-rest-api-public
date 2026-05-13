package org.derleta.nebula.account.application.port.in;

import org.derleta.nebula.account.application.port.in.command.RegisterAccountCommand;
import org.derleta.nebula.account.domain.model.AccountOperationResult;

public interface RegisterAccountUseCase {

    AccountOperationResult register(RegisterAccountCommand command);

}
