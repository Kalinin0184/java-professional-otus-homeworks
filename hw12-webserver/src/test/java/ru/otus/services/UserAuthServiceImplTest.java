package ru.otus.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.dao.InMemoryUserDao;

import static org.assertj.core.api.Assertions.assertThat;

class UserAuthServiceImplTest {

    private final UserAuthService authService = new UserAuthServiceImpl(new InMemoryUserDao());

    @Test
    @DisplayName("админ аутентифицируется с верным паролем")
    void shouldAuthenticateAdmin() {
        assertThat(authService.authenticate("admin", "admin")).isTrue();
        assertThat(authService.authenticate("admin", "wrong")).isFalse();
        assertThat(authService.authenticate("unknown", "admin")).isFalse();
    }
}
