package stroom.security.identity.db;

import stroom.db.util.JooqUtil;
import stroom.security.identity.db.jooq.tables.TokenType;
import stroom.security.identity.token.KeyTypeDao;

import jakarta.inject.Inject;

import java.util.Optional;

class KeyTypeDaoImpl implements KeyTypeDao {

    private final IdentityDbConnProvider identityDbConnProvider;

    @Inject
    KeyTypeDaoImpl(final IdentityDbConnProvider identityDbConnProvider) {
        this.identityDbConnProvider = identityDbConnProvider;
    }

    @Override
    public int getTypeId(final String type) {
        final Optional<Integer> result = get(type);
        if (result.isPresent()) {
            return result.get();
        }

        create(type);
        return get(type).orElse(-1);
    }

    private void create(final String type) {
        JooqUtil.onDuplicateKeyIgnore(() ->
                JooqUtil.context(identityDbConnProvider, context -> context
                        .insertInto(TokenType.TOKEN_TYPE, TokenType.TOKEN_TYPE.TYPE)
                        .values(type)
                        .execute()));
    }

    private Optional<Integer> get(final String type) {
        return JooqUtil.contextResult(identityDbConnProvider, context -> context
                        .select(TokenType.TOKEN_TYPE.ID)
                        .from(TokenType.TOKEN_TYPE)
                        .where(TokenType.TOKEN_TYPE.TYPE.eq(type))
                        .fetchOptional())
                .map(r -> r.getValue(TokenType.TOKEN_TYPE.ID));
    }
}
