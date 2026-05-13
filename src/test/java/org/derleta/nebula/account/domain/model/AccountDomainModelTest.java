package org.derleta.nebula.account.domain.model;

import org.derleta.nebula.account.domain.types.AccountResponseType;
import org.junit.jupiter.api.Test;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

class AccountDomainModelTest {

    @Test
    void account_recordCreation_fieldsAccessible() {
        Date d = new Date();
        Account a = new Account(1L, "e@t.com", "user", d, 1, 2);
        assertEquals(1L, a.id());
        assertEquals("e@t.com", a.email());
        assertEquals("user", a.login());
        assertEquals(d, a.birthDate());
        assertEquals(1, a.nationalityId());
        assertEquals(2, a.genderId());
    }

    @Test
    void accountOperationResult_successAndType() {
        AccountOperationResult r = new AccountOperationResult(true, AccountResponseType.ACCOUNT_CONFIRMED);
        assertTrue(r.success());
        assertEquals(AccountResponseType.ACCOUNT_CONFIRMED, r.type());
    }

    @Test
    void registeredAccountInfo_fieldsAccessible() {
        RegisteredAccountInfo info = new RegisteredAccountInfo(42L, "user", "e@t.com");
        assertEquals(42L, info.userId());
        assertEquals("e@t.com", info.email());
        assertEquals("user", info.username());
    }

    @Test
    void tokenResult_fieldsAccessible() {
        TokenResult t = new TokenResult("user", "e@t.com", "acc", "ref");
        assertEquals("user", t.username());
        assertEquals("e@t.com", t.email());
        assertEquals("acc", t.accessTokenCookie());
        assertEquals("ref", t.refreshTokenCookie());
    }
}
