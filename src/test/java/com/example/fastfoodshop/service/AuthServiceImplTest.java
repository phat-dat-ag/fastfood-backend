package com.example.fastfoodshop.service;

import com.example.fastfoodshop.entity.User;
import com.example.fastfoodshop.exception.auth.InvalidPasswordException;
import com.example.fastfoodshop.exception.auth.InvalidUserStatusException;
import com.example.fastfoodshop.exception.user.UserNotFoundException;
import com.example.fastfoodshop.factory.auth.SignInRequestFactory;
import com.example.fastfoodshop.factory.user.UserFactory;
import com.example.fastfoodshop.request.SignInRequest;
import com.example.fastfoodshop.response.auth.SignInResponse;
import com.example.fastfoodshop.security.JwtUtil;
import com.example.fastfoodshop.service.implementation.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {
    @Mock
    PasswordEncoder passwordEncoder;

    @Spy
    JwtUtil jwtUtil;

    @Mock
    UserService userService;

    @InjectMocks
    AuthServiceImpl authService;

    private static final String USER_PHONE = "0999991111";

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(
                jwtUtil,
                "jwtSecret",
                "my-super-secret-key-12345678901234567890123456789012345678901234567890"
        );
        ReflectionTestUtils.setField(jwtUtil, "jwtExpirationMs", 3600000);
    }

    @Test
    void signIn_validRequest_shouldReturnSignInResponse() {
        User validUser = UserFactory.createActivatedUser();

        SignInRequest signInRequest = SignInRequestFactory.createValid(validUser.getPhone());

        when(userService.findUserOrThrow(validUser.getPhone())).thenReturn(validUser);

        when(passwordEncoder.matches(
                signInRequest.password(), validUser.getPasswordHash())
        ).thenReturn(true);

        SignInResponse signInResponse = authService.signIn(signInRequest);

        assertNotNull(signInResponse);
        assertNotNull(signInResponse.user());
        assertNotNull(signInResponse.token());

        assertEquals(validUser.getPhone(), signInResponse.user().phone());

        verify(userService).findUserOrThrow(validUser.getPhone());
        verify(passwordEncoder).matches(signInRequest.password(), validUser.getPasswordHash());
    }

    @Test
    void signIn_userNotFound_shouldThrowUserNotFoundException() {
        SignInRequest signInRequest = SignInRequestFactory.createValid(USER_PHONE);

        when(userService.findUserOrThrow(signInRequest.phone()))
                .thenThrow(new UserNotFoundException(signInRequest.phone()));

        assertThrows(UserNotFoundException.class, () -> userService.findUserOrThrow(signInRequest.phone()));

        verify(userService).findUserOrThrow(signInRequest.phone());
    }

    @Test
    void signIn_deactivatedUser_shouldThrowInvalidUserStatusException() {
        User deactivatedUser = UserFactory.createDeactivatedUser();

        SignInRequest signInRequest = SignInRequestFactory.createValid(deactivatedUser.getPhone());

        when(userService.findUserOrThrow(deactivatedUser.getPhone())).thenReturn(deactivatedUser);

        assertThrows(InvalidUserStatusException.class, () -> authService.signIn(signInRequest));

        verify(userService).findUserOrThrow(deactivatedUser.getPhone());
    }

    @Test
    void signIn_deletedUser_shouldThrowInvalidUserStatusException() {
        User deletedUser = UserFactory.createDeletedUser();

        SignInRequest signInRequest = SignInRequestFactory.createValid(deletedUser.getPhone());

        when(userService.findUserOrThrow(deletedUser.getPhone())).thenReturn(deletedUser);

        assertThrows(InvalidUserStatusException.class, () -> authService.signIn(signInRequest));

        verify(userService).findUserOrThrow(deletedUser.getPhone());
    }

    @Test
    void signIn_incorrectPassword_shouldThrowInvalidPasswordException() {
        User validUser = UserFactory.createActivatedUser();

        SignInRequest signInRequest = SignInRequestFactory.createValid(validUser.getPhone());

        when(userService.findUserOrThrow(validUser.getPhone())).thenReturn(validUser);

        when(passwordEncoder.matches(
                signInRequest.password(), validUser.getPasswordHash())
        ).thenReturn(false);

        assertThrows(InvalidPasswordException.class, () -> authService.signIn(signInRequest));

        verify(userService).findUserOrThrow(validUser.getPhone());
        verify(passwordEncoder).matches(signInRequest.password(), validUser.getPasswordHash());
    }

    @Test
    void verify_validAuthentication_shouldReturnSignInResponse() {
        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);

        User user = UserFactory.createActivatedUser();

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user.getPhone());
        when(userService.findUserOrThrow(user.getPhone())).thenReturn(user);

        SignInResponse signInResponse = authService.verify(authentication);

        assertNotNull(signInResponse);
        assertNotNull(signInResponse.user());
        assertNotNull(signInResponse.token());

        assertEquals(user.getPhone(), signInResponse.user().phone());

        verify(userService).findUserOrThrow(user.getPhone());
    }

    @Test
    void verify_userNotFound_shouldThrowUserNotFoundException() {
        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(USER_PHONE);
        when(userService.findUserOrThrow(USER_PHONE))
                .thenThrow(new UserNotFoundException(USER_PHONE));

        assertThrows(UserNotFoundException.class, () -> authService.verify(authentication));

        verify(userService).findUserOrThrow(USER_PHONE);
    }

    @Test
    void verify_deletedUser_shouldThrowInvalidUserStatusException() {
        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);

        User deletedUser = UserFactory.createDeletedUser();

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(deletedUser.getPhone());
        when(userService.findUserOrThrow(deletedUser.getPhone())).thenReturn(deletedUser);

        assertThrows(InvalidUserStatusException.class, () -> authService.verify(authentication));

        verify(userService).findUserOrThrow(deletedUser.getPhone());
    }

    @Test
    void verify_deactivatedUser_shouldThrowInvalidUserStatusException() {
        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);

        User deactivatedUser = UserFactory.createDeletedUser();

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(deactivatedUser.getPhone());
        when(userService.findUserOrThrow(deactivatedUser.getPhone())).thenReturn(deactivatedUser);

        assertThrows(InvalidUserStatusException.class, () -> authService.verify(authentication));

        verify(userService).findUserOrThrow(deactivatedUser.getPhone());
    }
}
