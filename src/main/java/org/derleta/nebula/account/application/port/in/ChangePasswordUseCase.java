package org.derleta.nebula.account.application.port.in;

import org.derleta.nebula.account.application.port.in.command.ChangePasswordCommand;
import org.derleta.nebula.account.domain.model.AccountOperationResult;

public interface ChangePasswordUseCase {

    AccountOperationResult changePassword(String jwtToken, ChangePasswordCommand command);

}
