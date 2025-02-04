package stroom.security.impl;

import stroom.security.mock.MockSecurityContext;
import stroom.security.shared.User;
import stroom.util.shared.UserDesc;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.atomic.AtomicReference;

@ExtendWith(MockitoExtension.class)
class TestUserServiceImpl {

    public static final String SUBJECT_ID = "user123";
    public static final String DISPLAY_NAME = "jbloggs";
    public static final String FULL_NAME = "Joe Bloggs";
    @Mock
    private UserDao mockUserDao;

    @Test
    void testGetOrCreateUser_new() {
        final UserServiceImpl userService = new UserServiceImpl(
                new MockSecurityContext(),
                mockUserDao,
                null,
                null,
                null,
                null,
                null,
                null,
                null);

        final UserDesc userDesc = UserDesc.builder(SUBJECT_ID)
                .build();

        final AtomicReference<User> onCreateConsumer = new AtomicReference<>();

        Mockito.when(mockUserDao.tryCreate(Mockito.any(User.class), Mockito.any()))
                .thenAnswer(invocation -> {
                    return invocation.getArgument(0);
                });

        final User user = userService.getOrCreateUser(userDesc, onCreateConsumer::set);

        Assertions.assertThat(user.getSubjectId())
                .isEqualTo(SUBJECT_ID);
        Assertions.assertThat(user.getDisplayName())
                .isEqualTo(SUBJECT_ID);  // No display name so use sub
        Assertions.assertThat(user.getFullName())
                .isNull();
    }

    @Test
    void testGetOrCreateUser_new2() {
        final UserServiceImpl userService = new UserServiceImpl(
                new MockSecurityContext(),
                mockUserDao,
                null,
                null,
                null,
                null,
                null,
                null,
                null);

        final UserDesc userDesc = UserDesc.builder(SUBJECT_ID)
                .displayName(DISPLAY_NAME)
                .fullName(FULL_NAME)
                .build();

        final AtomicReference<User> onCreateConsumer = new AtomicReference<>();

        Mockito.when(mockUserDao.tryCreate(Mockito.any(User.class), Mockito.any()))
                .thenAnswer(invocation -> {
                    return invocation.getArgument(0);
                });

        final User user = userService.getOrCreateUser(userDesc, onCreateConsumer::set);

        Assertions.assertThat(user.getSubjectId())
                .isEqualTo(SUBJECT_ID);
        Assertions.assertThat(user.getDisplayName())
                .isEqualTo(DISPLAY_NAME);  // No display name so use sub
        Assertions.assertThat(user.getFullName())
                .isEqualTo(FULL_NAME);
    }
}
