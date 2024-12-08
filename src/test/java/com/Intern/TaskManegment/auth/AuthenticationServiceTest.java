package com.Intern.TaskManegment.auth;

import com.Intern.TaskManegment.config.JwtService;
import com.Intern.TaskManegment.model.User;
import com.Intern.TaskManegment.model.enums.Role;
import com.Intern.TaskManegment.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    public void shouldRegisterUserSuccessfully() {
        // given
        RegisterRequest registerRequest = RegisterRequest.builder()
                .name("Test User")
                .email("test@example.com")
                .password("password123")
                .build();

        User user = User.builder()
                .name(registerRequest.getName())
                .email(registerRequest.getEmail())
                .password("encodedPassword")
                .role(Role.USER)
                .build();

        // Убедитесь, что все используемые объекты соответствуют типу User
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(Mockito.any(User.class))).thenReturn(user);  // Мокируем save для User

        // Используем any(UserDetails.class), так как jwtService ожидает объект UserDetails
        when(jwtService.generateToken(Mockito.any(org.springframework.security.core.userdetails.UserDetails.class))).thenReturn("mockJwtToken");

        // when
        AuthenticationResponse response = authenticationService.register(registerRequest);

        // then
        Assertions.assertNotNull(response);
        Assertions.assertEquals("mockJwtToken", response.getToken());
    }

    @Test
    public void shouldAuthenticateUserSuccessfully() {
        // given
        AuthenticationRequest authRequest = AuthenticationRequest.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        User user = User.builder()
                .email(authRequest.getEmail())
                .password("encodedPassword")
                .role(Role.USER)
                .build();

        when(userRepository.findByEmail(authRequest.getEmail())).thenReturn(Optional.of(user));

        // Используем any(Authentication.class), так как authenticate ожидает Authentication
        when(authenticationManager.authenticate(Mockito.any(Authentication.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));

        // Используем any(UserDetails.class), так как jwtService ожидает UserDetails
        when(jwtService.generateToken(Mockito.any(org.springframework.security.core.userdetails.UserDetails.class))).thenReturn("mockJwtToken");

        // when
        AuthenticationResponse response = authenticationService.authenticate(authRequest);

        // then
        Assertions.assertNotNull(response);
        Assertions.assertEquals("mockJwtToken", response.getToken());
    }
}
