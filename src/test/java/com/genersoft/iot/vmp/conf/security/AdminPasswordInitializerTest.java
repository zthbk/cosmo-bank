package com.genersoft.iot.vmp.conf.security;

import com.genersoft.iot.vmp.service.IUserService;
import com.genersoft.iot.vmp.storager.dao.dto.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminPasswordInitializerTest {

    @Mock
    private IUserService userService;

    @Mock
    private PasswordComplexityValidator passwordComplexityValidator;

    @InjectMocks
    private AdminPasswordInitializer initializer;

    private User admin;

    @BeforeEach
    void setUp() {
        admin = new User();
        admin.setId(1);
        admin.setUsername("admin");
        admin.setDefaultPassword(true);
    }

    @Test
    void shouldSetPasswordWhenAdminStillDefault() {
        when(userService.getUserByUsername("admin")).thenReturn(admin);
        when(passwordComplexityValidator.validatePassword("Admin@123456", "admin")).thenReturn(true);
        when(userService.changePassword(1, DigestUtils.md5DigestAsHex(
                "Admin@123456".getBytes(StandardCharsets.UTF_8)))).thenReturn(true);
        ReflectionTestUtils.setField(initializer, "initialPassword", "Admin@123456");

        initializer.run(null);

        verify(userService).changePassword(1, DigestUtils.md5DigestAsHex(
                "Admin@123456".getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    void shouldNotOverrideAlreadyChangedPassword() {
        admin.setDefaultPassword(false);
        when(userService.getUserByUsername("admin")).thenReturn(admin);
        ReflectionTestUtils.setField(initializer, "initialPassword", "Admin@123456");

        initializer.run(null);

        verify(userService, never()).changePassword(anyInt(), anyString());
    }

    @Test
    void shouldNotSetWeakPassword() {
        when(userService.getUserByUsername("admin")).thenReturn(admin);
        when(passwordComplexityValidator.validatePassword("weak", "admin")).thenReturn(false);
        ReflectionTestUtils.setField(initializer, "initialPassword", "weak");

        initializer.run(null);

        verify(userService, never()).changePassword(anyInt(), anyString());
    }
}
