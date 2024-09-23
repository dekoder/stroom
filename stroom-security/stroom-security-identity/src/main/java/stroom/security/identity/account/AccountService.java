package stroom.security.identity.account;

import stroom.security.identity.shared.Account;
import stroom.security.identity.shared.AccountResultPage;
import stroom.security.identity.shared.CreateAccountRequest;
import stroom.security.identity.shared.FindAccountRequest;
import stroom.security.identity.shared.UpdateAccountRequest;

import java.util.Optional;

public interface AccountService {

    AccountResultPage list();

    AccountResultPage search(FindAccountRequest request);

    Account create(CreateAccountRequest request);

    Optional<Account> read(int accountId);

    Optional<Account> read(String email);

    void update(UpdateAccountRequest request, int accountId);

    void delete(int accountId);
}
