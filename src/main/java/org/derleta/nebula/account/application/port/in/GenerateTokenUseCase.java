package org.derleta.nebula.account.application.port.in;

import org.derleta.nebula.account.domain.model.TokenResult;

public interface GenerateTokenUseCase {

    TokenResult generateToken(String email, String password);

}
