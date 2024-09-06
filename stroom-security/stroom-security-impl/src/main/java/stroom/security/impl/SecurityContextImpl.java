package stroom.security.impl;

import stroom.docref.DocRef;
import stroom.docrefinfo.api.DocRefInfoService;
import stroom.security.api.SecurityContext;
import stroom.security.api.UserIdentity;
import stroom.security.api.UserIdentityFactory;
import stroom.security.api.exception.AuthenticationException;
import stroom.security.shared.DocumentPermissionNames;
import stroom.security.shared.HasStroomUserIdentity;
import stroom.security.shared.PermissionNames;
import stroom.security.shared.User;
import stroom.util.NullSafe;
import stroom.util.logging.LambdaLogger;
import stroom.util.logging.LambdaLoggerFactory;
import stroom.util.logging.LogUtil;
import stroom.util.shared.DocumentOwnerException;
import stroom.util.shared.PermissionException;

import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.inject.Singleton;

import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

@Singleton
class SecurityContextImpl implements SecurityContext {

    private static final LambdaLogger LOGGER = LambdaLoggerFactory.getLogger(SecurityContextImpl.class);

    private final ThreadLocal<Boolean> checkTypeThreadLocal = ThreadLocal.withInitial(() -> Boolean.TRUE);

    private final UserDocumentPermissionsCache userDocumentPermissionsCache;
    private final DocumentOwnerPermissionsCache documentOwnerPermissionsCache;
    private final UserGroupsCache userGroupsCache;
    private final UserAppPermissionsCache userAppPermissionsCache;
    private final Provider<UserCache> userCacheProvider;
    private final UserIdentityFactory userIdentityFactory;
    private final DocRefInfoService docRefInfoService;

    @Inject
    SecurityContextImpl(
            final UserDocumentPermissionsCache userDocumentPermissionsCache,
            final DocumentOwnerPermissionsCache documentOwnerPermissionsCache,
            final UserGroupsCache userGroupsCache,
            final UserAppPermissionsCache userAppPermissionsCache,
            final Provider<UserCache> userCacheProvider,
            final UserIdentityFactory userIdentityFactory,
            final DocRefInfoService docRefInfoService) {
        this.userDocumentPermissionsCache = userDocumentPermissionsCache;
        this.documentOwnerPermissionsCache = documentOwnerPermissionsCache;
        this.userGroupsCache = userGroupsCache;
        this.userAppPermissionsCache = userAppPermissionsCache;
        this.userCacheProvider = userCacheProvider;
        this.userIdentityFactory = userIdentityFactory;
        this.docRefInfoService = docRefInfoService;
    }

    @Override
    public String getSubjectId() {
        final UserIdentity userIdentity = getUserIdentity();
        if (userIdentity == null) {
            return null;
        }
        return userIdentity.getSubjectId();
    }

    @Override
    public String getUserUuid() {
        final UserIdentity userIdentity = getUserIdentity();
        if (userIdentity == null) {
            return null;
        }
        return getUserUuid(userIdentity);
    }

    @Override
    public UserIdentity getUserIdentity() {
        return CurrentUserState.current();
    }

    @Override
    public UserIdentity getOrCreateUserIdentity(final String subjectId) {
        Objects.requireNonNull(subjectId, "Null subjectId provided");
        // Inject as provider to avoid circular dep issues
        return userCacheProvider.get().getOrCreate(subjectId)
                .map(BasicUserIdentity::new)
                .orElseThrow(() -> new AuthenticationException("Unable to find user with id=" + subjectId));
    }

    @Override
    public UserIdentity getIdentityBySubjectId(final String subjectId, final boolean isGroup) {
        // TODO this method can probably go when we make subject fully unique
        //  Added temporarily to enable content import to run as groups or users.
        Objects.requireNonNull(subjectId, "Null user uuid provided");
        // Inject as provider to avoid circular dep issues
        return userCacheProvider.get().get(subjectId, isGroup)
                .map(BasicUserIdentity::new)
                .orElseThrow(() -> new AuthenticationException(LogUtil.message(
                        "Unable to find {} with uuid: {}",
                        (isGroup
                                ? "group"
                                : "user"),
                        subjectId)));
    }

    @Override
    public UserIdentity getIdentityByUserUuid(final String userUuid) {
        Objects.requireNonNull(userUuid, "Null user uuid provided");
        // Inject as provider to avoid circular dep issues
        return userCacheProvider.get().getByUuid(userUuid)
                .map(BasicUserIdentity::new)
                .orElseThrow(() -> new AuthenticationException("Unable to find user with uuid=" + userUuid));
    }

    @Override
    public boolean isLoggedIn() {
        return getUserIdentity() != null;
    }

    @Override
    public boolean isAdmin() {
        return hasAppPermission(PermissionNames.ADMINISTRATOR);
    }

    @Override
    public boolean isProcessingUser() {
        // Get the current user.
        final UserIdentity userIdentity = getUserIdentity();

        // If there is no logged in user then throw an exception.
        if (userIdentity == null) {
            throw new AuthenticationException("No user is currently logged in");
        }

        // If the user is the internal processing user then they automatically have permission.
        return userIdentityFactory.isServiceUser(userIdentity);
    }

    @Override
    public boolean isUseAsRead() {
        return CurrentUserState.isElevatePermissions();
    }

    private String getUserUuid(final UserIdentity userIdentity) {
        if (userIdentity instanceof final HasStroomUserIdentity hasStroomUserIdentity) {
            final String userUuid = hasStroomUserIdentity.getUuid();

            if (NullSafe.isBlankString(userUuid)) {
                throw new AuthenticationException("Missing user UUID value");
            }

            return userUuid;
        } else {
            throw new AuthenticationException(LogUtil.message(
                    "Expecting a stroom user identity (i.e. {}), but got {}",
                    HasStroomUserIdentity.class.getSimpleName(),
                    userIdentity.getClass().getSimpleName()));
        }
    }

    private void pushUser(final UserIdentity userIdentity) {
        // Before we push the user see if we need to refresh the user token.
        userIdentityFactory.refresh(userIdentity);
        // Push the user.
        CurrentUserState.push(userIdentity);
    }

    private void popUser() {
        CurrentUserState.pop();
    }

    private void elevatePermissions() {
        CurrentUserState.elevatePermissions();
    }

    private void restorePermissions() {
        CurrentUserState.restorePermissions();
    }

    @Override
    public boolean hasAppPermission(final String permission) {
        // Get the current user.
        final UserIdentity userIdentity = getUserIdentity();

        // If there is no logged in user then throw an exception.
        if (userIdentity == null) {
            throw new AuthenticationException("No user is currently logged in");
        }

        // If the user is the internal processing user then they automatically have permission.
        if (userIdentityFactory.isServiceUser(userIdentity)) {
            return true;
        }

        // See if the user has permission.
        final String userUuid = getUserUuid(userIdentity);
        boolean result = hasAppPermission(userUuid, permission);

        // If the user doesn't have the requested permission see if they are an admin.
        if (!result && !PermissionNames.ADMINISTRATOR.equals(permission)) {
            result = hasAppPermission(userUuid, PermissionNames.ADMINISTRATOR);
        }

        return result;
    }

    private boolean hasAppPermission(final String userUuid, final String permission) {
        // See if the user has an explicit permission.
        if (hasUserAppPermission(userUuid, permission)) {
            return true;
        }

        // See if the user belongs to a group that has permission.
        final Set<String> userGroupUuids = userGroupsCache.get(userUuid);
        if (userGroupUuids != null) {
            for (final String userGroupUuid : userGroupUuids) {
                if (hasUserAppPermission(userGroupUuid, permission)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasUserAppPermission(final String userUuid, final String permission) {
        final Set<String> userAppPermissions = userAppPermissionsCache.get(userUuid);
        if (userAppPermissions != null) {
            return userAppPermissions.contains(permission);
        }
        return false;
    }

    @Override
    public boolean hasDocumentPermission(final String documentUuid, final String permission) {
        // Let administrators do anything.
        if (isAdmin()) {
            return true;
        }

        // Get the current user.
        final UserIdentity userIdentity = getUserIdentity();

        // If there is no logged in user then throw an exception.
        if (userIdentity == null) {
            throw new AuthenticationException("No user is currently logged in");
        }

        // If we are currently allowing users with only `Use` permission to `Read` (elevate permissions) then
        // test for `Use` instead of `Read`.
        final String perm = DocumentPermissionNames.READ.equals(permission) && CurrentUserState.isElevatePermissions()
                ? DocumentPermissionNames.USE
                : permission;

        final String userUuid = getUserUuid(userIdentity);
        return hasDocumentPermission(userUuid, documentUuid, perm);
    }

    private boolean hasDocumentPermission(final String userUuid, final String documentUuid, final String permission) {
        // See if the user has an explicit permission.
        if (hasUserDocumentPermission(userUuid, documentUuid, permission)) {
            return true;
        }

        // See if the user belongs to a group that has permission.
        final Set<String> userGroupUuids = userGroupsCache.get(userUuid);
        if (userGroupUuids != null) {
            for (final String userGroupUuid : userGroupUuids) {
                if (hasUserDocumentPermission(userGroupUuid, documentUuid, permission)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasUserDocumentPermission(final String userUuid,
                                              final String documentUuid,
                                              final String permission) {
        final UserDocumentPermissions userDocumentPermissions = userDocumentPermissionsCache.get(userUuid);
        if (userDocumentPermissions != null) {
            return userDocumentPermissions.hasDocumentPermission(documentUuid, permission);
        }
        return false;
    }

    @Override
    public String getDocumentOwnerUuid(final DocRef docRef) {
        Objects.requireNonNull(docRef, "docRef not provided");
        final String documentUuid = docRef.getUuid();
        final Set<String> ownerStroomUserUuids = documentOwnerPermissionsCache.get(documentUuid);
        if (NullSafe.isEmptyCollection(ownerStroomUserUuids)) {
            throw new DocumentOwnerException(
                    documentUuid,
                    LogUtil.message("Document {} has no owners. Either assign an Owner to it or " +
                                    "if there is one, then try clearing cache '{}'",
                            decorateDocRefForLogging(docRef),
                            DocumentOwnerPermissionsCache.CACHE_NAME));
        } else if (ownerStroomUserUuids.size() > 1) {
            throw new DocumentOwnerException(
                    documentUuid,
                    LogUtil.message("Document {} has multiple ({}) owners. There can be only one. " +
                                    "Owner user uuids: [{}]. ",
                            decorateDocRefForLogging(docRef),
                            ownerStroomUserUuids.size(),
                            String.join(", ", ownerStroomUserUuids)));
        }
        return ownerStroomUserUuids.iterator().next();
    }

    private DocRef decorateDocRefForLogging(final DocRef docRef) {
        if (docRef == null) {
            return null;
        } else {
            try {
                return docRefInfoService.decorate(docRef);
            } catch (Exception e) {
                // Failure should not be re-thrown
                LOGGER.debug("Error decorating docRef {}", docRef, e);
                return docRef;
            }
        }
    }

    /**
     * Run the supplied code as the specified user.
     */
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

    /**
     * Run the supplied code as the specified user.
     */
    @Override
    public void asUser(final UserIdentity userIdentity, final Runnable runnable) {
        boolean success = false;
        try {
            pushUser(userIdentity);
            success = true;
            runnable.run();
        } finally {
            if (success) {
                popUser();
            }
        }
    }

    /**
     * Run the supplied code as the internal processing user.
     */
    @Override
    public <T> T asProcessingUserResult(final Supplier<T> supplier) {
        return asUserResult(userIdentityFactory.getServiceUserIdentity(), supplier);
    }

    /**
     * Run the supplied code as the internal processing user.
     */
    @Override
    public void asProcessingUser(final Runnable runnable) {
        asUser(userIdentityFactory.getServiceUserIdentity(), runnable);
    }

    /**
     * Run the supplied code as an admin user.
     */
    @Override
    public <T> T asAdminUserResult(final Supplier<T> supplier) {
        return asUserResult(getOrCreateUserIdentity(User.ADMIN_USER_SUBJECT_ID), supplier);
    }

    /**
     * Run the supplied code as an admin user.
     */
    @Override
    public void asAdminUser(final Runnable runnable) {
        asUser(getOrCreateUserIdentity(User.ADMIN_USER_SUBJECT_ID), runnable);
    }

    /**
     * Allow the current user to read items that they only have 'Use' permission on.
     */
    @Override
    public <T> T useAsReadResult(final Supplier<T> supplier) {
        T result;
        boolean success = false;
        try {
            elevatePermissions();
            success = true;
            result = supplier.get();
        } finally {
            if (success) {
                restorePermissions();
            }
        }
        return result;
    }

    /**
     * Allow the current user to read items that they only have 'Use' permission on.
     */
    @Override
    public void useAsRead(final Runnable runnable) {
        boolean success = false;
        try {
            elevatePermissions();
            success = true;
            runnable.run();
        } finally {
            if (success) {
                restorePermissions();
            }
        }
    }

    /**
     * Secure the supplied code with the supplied application permission.
     */
    @Override
    public void secure(final String permission, final Runnable runnable) {
        // Initiate current check type.
        final Boolean currentCheckType = checkTypeThreadLocal.get();

        // If we aren't currently checking anything then just proceed.
        if (Boolean.FALSE.equals(currentCheckType)) {
            runnable.run();
        } else {
            // If the current user is an administrator then don't do any security checking.
            if (permission == null || checkAdmin()) {
                try {
                    // Don't check any further permissions.
                    checkTypeThreadLocal.set(Boolean.FALSE);
                    runnable.run();
                } finally {
                    // Reset the current check type.
                    checkTypeThreadLocal.set(currentCheckType);
                }
            } else {
                // We must be logged in to access a secured service.
                checkLogin();
                checkAppPermission(permission);

                runnable.run();
            }
        }
    }

    /**
     * Secure the supplied code with the supplied application permission.
     */
    @Override
    public <T> T secureResult(final String permission, final Supplier<T> supplier) {
        T result;

        // Initiate current check type.
        final Boolean currentCheckType = checkTypeThreadLocal.get();

        // If we aren't currently checking anything then just proceed.
        if (Boolean.FALSE.equals(currentCheckType)) {
            result = supplier.get();
        } else {
            // If the current user is an administrator then don't do any security checking.
            if (permission == null || checkAdmin()) {
                try {
                    // Don't check any further permissions.
                    checkTypeThreadLocal.set(Boolean.FALSE);
                    result = supplier.get();
                } finally {
                    // Reset the current check type.
                    checkTypeThreadLocal.set(currentCheckType);
                }
            } else {
                // We must be logged in to access a secured service.
                checkLogin();
                checkAppPermission(permission);

                result = supplier.get();
            }
        }

        return result;
    }

    /**
     * Secure the supplied code to ensure that there is a current authenticated user.
     */
    @Override
    public void secure(final Runnable runnable) {
        // Initiate current check type.
        final Boolean currentCheckType = checkTypeThreadLocal.get();

        // If we aren't currently checking anything then just proceed.
        if (Boolean.FALSE.equals(currentCheckType)) {
            runnable.run();
        } else {
            // If the current user is an administrator then don't do any security checking.
            if (checkAdmin()) {
                try {
                    // Don't check any further permissions.
                    checkTypeThreadLocal.set(Boolean.FALSE);
                    runnable.run();
                } finally {
                    // Reset the current check type.
                    checkTypeThreadLocal.set(currentCheckType);
                }
            } else {
                // We must be logged in to access a secured service.
                checkLogin();

                runnable.run();
            }
        }
    }

    /**
     * Secure the supplied code to ensure that there is a current authenticated user.
     */
    @Override
    public <T> T secureResult(final Supplier<T> supplier) {
        T result;

        // Initiate current check type.
        final Boolean currentCheckType = checkTypeThreadLocal.get();

        // If we aren't currently checking anything then just proceed.
        if (Boolean.FALSE.equals(currentCheckType)) {
            result = supplier.get();
        } else {
            // If the current user is an administrator then don't do any security checking.
            if (checkAdmin()) {
                try {
                    // Don't check any further permissions.
                    checkTypeThreadLocal.set(Boolean.FALSE);
                    result = supplier.get();
                } finally {
                    // Reset the current check type.
                    checkTypeThreadLocal.set(currentCheckType);
                }
            } else {
                // We must be logged in to access a secured service.
                checkLogin();

                result = supplier.get();
            }
        }

        return result;
    }

    /**
     * Run the supplied code regardless of whether there is a current user and also allow all inner code to run
     * insecurely even if it is often secured when executed from other entry points.
     */
    @Override
    public void insecure(final Runnable runnable) {
        secure(null, runnable);
    }

    /**
     * Run the supplied code regardless of whether there is a current user and also allow all inner code to run
     * insecurely even if it is often secured when executed from other entry points.
     */
    @Override
    public <T> T insecureResult(final Supplier<T> supplier) {
        return secureResult(null, supplier);
    }

    private void checkAppPermission(final String permission) {
        final Boolean currentCheckType = checkTypeThreadLocal.get();
        try {
            // Don't check any further permissions.
            checkTypeThreadLocal.set(Boolean.FALSE);
            if (!hasAppPermission(permission)) {
                throw new PermissionException(
                        getUserIdentityForAudit(),
                        "User does not have the required permission (" + permission + ")");
            }
        } finally {
            checkTypeThreadLocal.set(currentCheckType);
        }
    }

    private void checkLogin() {
        final Boolean currentCheckType = checkTypeThreadLocal.get();
        try {
            // Don't check any further permissions.
            checkTypeThreadLocal.set(Boolean.FALSE);
            if (!isLoggedIn()) {
                throw new PermissionException(
                        getUserIdentityForAudit(),
                        "A user must be logged in to call service");
            }
        } finally {
            checkTypeThreadLocal.set(currentCheckType);
        }
    }

    private boolean checkAdmin() {
        final Boolean currentCheckType = checkTypeThreadLocal.get();
        try {
            // Don't check any further permissions.
            checkTypeThreadLocal.set(Boolean.FALSE);
            return isAdmin();
        } finally {
            checkTypeThreadLocal.set(currentCheckType);
        }
    }
}
