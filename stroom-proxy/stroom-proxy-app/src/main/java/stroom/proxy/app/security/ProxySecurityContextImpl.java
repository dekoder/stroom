package stroom.proxy.app.security;

import stroom.security.api.CommonSecurityContext;
import stroom.security.api.UserIdentity;
import stroom.security.api.UserIdentityFactory;
import stroom.security.api.exception.AuthenticationException;
import stroom.security.shared.AppPermission;
import stroom.security.shared.AppPermissionSet;
import stroom.security.shared.VerifyApiKeyRequest;
import stroom.util.logging.LambdaLogger;
import stroom.util.logging.LambdaLoggerFactory;
import stroom.util.logging.LogUtil;

import jakarta.inject.Inject;

import java.util.Objects;
import java.util.function.Supplier;

public class ProxySecurityContextImpl implements CommonSecurityContext {

    private static final LambdaLogger LOGGER = LambdaLoggerFactory.getLogger(ProxySecurityContextImpl.class);
    private static final ThreadLocal<CheckType> CHECK_TYPE_THREAD_LOCAL = ThreadLocal.withInitial(() ->
            CheckType.CHECK);
    private static final AppPermissionSet ADMIN_APP_PERMISSIONS = AppPermission.ADMINISTRATOR.asAppPermissionSet();

    private final UserIdentityFactory userIdentityFactory;
    private final ProxyApiKeyService proxyApiKeyService;

    @Inject
    public ProxySecurityContextImpl(final UserIdentityFactory userIdentityFactory,
                                    final ProxyApiKeyService proxyApiKeyService) {
        this.userIdentityFactory = userIdentityFactory;
        this.proxyApiKeyService = proxyApiKeyService;
    }

    @Override
    public UserIdentity getUserIdentity() {
        return ProxyCurrentUserState.current();
    }

    @Override
    public boolean isAdmin() {
        return hasAppPermissions(ADMIN_APP_PERMISSIONS);
    }

    @Override
    public boolean isProcessingUser() {
        // Get the current user.
        final UserIdentity userIdentity = assertUserIdentity();

        // If the user is the internal processing user then they automatically have permission.
        return isProcessingUser(userIdentity);
    }

    @Override
    public boolean hasAppPermissions(final AppPermissionSet requiredPermission) {
        // Get the current user.
        final UserIdentity userIdentity = assertUserIdentity();
        return hasAppPermissions(userIdentity, requiredPermission);
    }

    @Override
    public boolean hasAppPermissions(final UserIdentity userIdentity,
                                     final AppPermissionSet requiredPermissions) {
        // If the user is the internal processing user then they automatically have permission.
        // This will also assert a user is 'logged in'.
        if (isProcessingUser(userIdentity)) {
            return true;
        }
        // If no required perms then no need to check anything else
        if (AppPermissionSet.isEmpty(requiredPermissions)) {
            return true;
        }

        if (userIdentity instanceof ApiKeyUserIdentity apiKeyUserIdentity) {
            // Have to do this as proc user because verifyApiKey needs perms to be called,
            // i.e. when called directly from a resource
            return asProcessingUserResult(() ->
                    proxyApiKeyService.verifyApiKey(new VerifyApiKeyRequest(
                                    apiKeyUserIdentity.getApiKey(),
                                    requiredPermissions))
                            .isPresent());
        } else {
            // TODO do we need to support perm checking for other user types, e.g. OAuth?
            return false;
        }
    }

    @Override
    public <T> T asUserResult(final UserIdentity userIdentity, final Supplier<T> supplier) {
        T result;
        boolean success = false;
        try {
            pushUser(userIdentity);
            success = true;
            result = supplier.get();
        } finally {
            if (success) {
                popUser();
            }
        }
        return result;
    }

//    @Override
//    public <T> T asUserResult(final UserRef userRef, final Supplier<T> supplier) {
////        ensureValidUser(userRef);
//        return asUserResult(new BasicUserIdentity(userRef), supplier);
//    }

//    @Override
//    public void asUser(final UserRef userRef, final Runnable runnable) {
//
//    }

    @Override
    public void asUser(final UserIdentity userIdentity, final Runnable runnable) {
        asUserResult(userIdentity, runnableAsSupplier(runnable));
    }

    @Override
    public <T> T asProcessingUserResult(final Supplier<T> supplier) {
        return asUserResult(userIdentityFactory.getServiceUserIdentity(), supplier);
    }

    @Override
    public void asProcessingUser(final Runnable runnable) {
        asUser(userIdentityFactory.getServiceUserIdentity(), runnable);
    }

//    @Override
//    public void secure(final AppPermission requiredPermission, final Runnable runnable) {
//        Objects.requireNonNull(requiredPermission);
//        doSecureWork(runnableAsSupplier(runnable),
//                () -> checkAppPermissionSet(requiredPermission.asAppPermissionSet()),
//                false);
//    }

    @Override
    public void secure(final AppPermissionSet requiredPermissions, final Runnable runnable) {
        Objects.requireNonNull(requiredPermissions);
        doSecureWork(runnableAsSupplier(runnable),
                () -> checkAppPermissionSet(requiredPermissions),
                false);
    }

//    @Override
//    public <T> T secureResult(final AppPermission requiredPermission, final Supplier<T> supplier) {
//        Objects.requireNonNull(requiredPermission);
//        return doSecureWork(supplier,
//                () -> checkAppPermissionSet(requiredPermission.asAppPermissionSet()),
//                false);
//    }

    @Override
    public <T> T secureResult(final AppPermissionSet requiredPermissions, final Supplier<T> supplier) {
        Objects.requireNonNull(requiredPermissions);
        return doSecureWork(
                supplier,
                () -> checkAppPermissionSet(requiredPermissions),
                false);
    }

    @Override
    public void secure(final Runnable runnable) {
        Objects.requireNonNull(runnable);
        doSecureWork(runnableAsSupplier(runnable), null, false);
    }

    @Override
    public <T> T secureResult(final Supplier<T> supplier) {
        Objects.requireNonNull(supplier);
        return doSecureWork(supplier, null, false);
    }

    @Override
    public void insecure(final Runnable runnable) {
        Objects.requireNonNull(runnable);
        doSecureWork(runnableAsSupplier(runnable), null, true);
    }

    @Override
    public <T> T insecureResult(final Supplier<T> supplier) {
        return doSecureWork(supplier, null, true);
    }

    private UserIdentity assertUserIdentity() {
        // Get the current user.
        final UserIdentity userIdentity = getUserIdentity();

        // If there is no logged in user then throw an exception.
        if (userIdentity == null) {
            throw new AuthenticationException("No user is currently logged in");
        }

        return userIdentity;
    }

    private boolean isProcessingUser(final UserIdentity userIdentity) {
        return userIdentityFactory.isServiceUser(userIdentity);
    }

    private void pushUser(final UserIdentity userIdentity) {
        // Before we push the user see if we need to refresh the user token.
//        userIdentityFactory.refresh(userIdentity);
        // Push the user.
        ProxyCurrentUserState.push(userIdentity);
    }

    private void popUser() {
        ProxyCurrentUserState.pop();
    }

    private Supplier<Void> runnableAsSupplier(final Runnable runnable) {
        return () -> {
            runnable.run();
            return null;
        };
    }

    /**
     * @param supplier        The work to perform
     * @param permissionCheck The permission check to perform or null if there isn't one
     * @param isInsecure      Can be set to true if, for example, the caller has been given
     *                        an empty {@link AppPermissionSet}
     */
    private <T> T doSecureWork(final Supplier<T> supplier,
                               final Runnable permissionCheck,
                               final boolean isInsecure) {
        Objects.requireNonNull(supplier);
        final T result;

        // Initiate current check type.
        final CheckType currentCheckType = CHECK_TYPE_THREAD_LOCAL.get();

        // If we aren't currently checking anything then just proceed.
        if (CheckType.DONT_CHECK == currentCheckType) {
            result = supplier.get();
        } else {
            // If the current user is an administrator then don't do any security checking.
            if (isInsecure || checkAdmin()) {
                try {
                    // Don't check any further permissions.
                    CHECK_TYPE_THREAD_LOCAL.set(CheckType.DONT_CHECK);
                    result = supplier.get();
                } finally {
                    // Reset the current check type.
                    CHECK_TYPE_THREAD_LOCAL.set(currentCheckType);
                }
            } else {
                // We must be logged in to access a secured service.
                checkLogin();
                if (permissionCheck != null) {
                    permissionCheck.run();
                }
                result = supplier.get();
            }
        }
        return result;
    }

    private void checkLogin() {
        final CheckType currentCheckType = CHECK_TYPE_THREAD_LOCAL.get();
        try {
            // Don't check any further permissions.
            CHECK_TYPE_THREAD_LOCAL.set(CheckType.DONT_CHECK);
            if (getUserIdentity() == null) {
                throw new AuthenticationException("A user/identity must have been authenticated");
            }
        } finally {
            CHECK_TYPE_THREAD_LOCAL.set(currentCheckType);
        }
    }

    private boolean checkAdmin() {
        final CheckType currentCheckType = CHECK_TYPE_THREAD_LOCAL.get();
        try {
            // Don't check any further permissions.
            CHECK_TYPE_THREAD_LOCAL.set(CheckType.DONT_CHECK);
            return isAdmin();
        } finally {
            CHECK_TYPE_THREAD_LOCAL.set(currentCheckType);
        }
    }

    private void checkAppPermissionSet(final AppPermissionSet requiredPermissions) {
        final CheckType currentCheckType = CHECK_TYPE_THREAD_LOCAL.get();
        try {
            // Don't check any further permissions.
            CHECK_TYPE_THREAD_LOCAL.set(CheckType.DONT_CHECK);

            final boolean hasPerms = hasAppPermissions(requiredPermissions);

            if (!hasPerms) {
                final UserIdentity userIdentity = LogUtil.swallowExceptions(this::getUserIdentity)
                        .orElse(null);
                throw new AuthenticationException(
                        LogUtil.message("User {} does not have the required permissions ({})",
                                userIdentity, requiredPermissions));
            }

//            if (AppPermissionSet.isEmpty(requiredPermissions)) {
//                // No perms required, so all fine
//            } else if (requiredPermissions.isAllOf()) {
//                if (!hasAppPermissions(requiredPermissions)) {
//                    final UserIdentity userIdentity = LogUtil.swallowExceptions(this::getUserIdentity)
//                            .orElse(null);
//                    throw new AuthenticationException(
//                            LogUtil.message("User {} does not have the required permissions ({})",
//                                    userIdentity, requiredPermissions));
//                }
//            } else {
//                // One of permissionSet must be held
//                boolean foundOne = requiredPermissions.stream()
//                        .anyMatch(this::hasAppPermission);
//                if (!foundOne) {
//                    final UserIdentity userIdentity = LogUtil.swallowExceptions(this::getUserIdentity)
//                            .orElse(null);
//                    throw new AuthenticationException(
//                            LogUtil.message("User {} does not have the required permissions ({})",
//                                    userIdentity, requiredPermissions));
//                }
//            }
        } finally {
            CHECK_TYPE_THREAD_LOCAL.set(currentCheckType);
        }
    }


    // --------------------------------------------------------------------------------


    private enum CheckType {
        CHECK,
        DONT_CHECK,
        ;
    }
}
