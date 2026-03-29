package com.example.fastfoodshop.service.implementation;

import com.example.fastfoodshop.dto.UserDTO;
import com.example.fastfoodshop.entity.User;
import com.example.fastfoodshop.exception.auth.InvalidPasswordException;
import com.example.fastfoodshop.exception.auth.InvalidUserStatusException;
import com.example.fastfoodshop.request.SignInRequest;
import com.example.fastfoodshop.response.auth.SignInResponse;
import com.example.fastfoodshop.security.JwtUtil;
import com.example.fastfoodshop.service.AuthService;
import com.example.fastfoodshop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    private void validateAccount(User user) {
        if (user.isDeleted()) {
            throw new InvalidUserStatusException("User is deleted");
        }

        if (!user.isActivated()) {
            throw new InvalidUserStatusException("User is not activated");
        }
    }

    private void validatePassword(User user, String password) {
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new InvalidPasswordException();
        }
    }

    private SignInResponse buildSignInResponse(User user) {
        String newToken = jwtUtil.generateToken(user);

        return new SignInResponse(newToken, UserDTO.from(user));
    }

    public SignInResponse signIn(SignInRequest signInRequest) {
        User user = userService.findUserOrThrow(signInRequest.phone());

        validateAccount(user);

        validatePassword(user, signInRequest.password());

        return buildSignInResponse(user);
    }

    public SignInResponse verify(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String phone = userDetails.getUsername();

        User user = userService.findUserOrThrow(phone);

        return buildSignInResponse(user);
    }
}
