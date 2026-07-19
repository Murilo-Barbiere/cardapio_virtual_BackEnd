package com.BarbiereDev.cardapio_virtual_BackEnd.service;

import com.BarbiereDev.cardapio_virtual_BackEnd.dto.request.LoginRequest;
import com.BarbiereDev.cardapio_virtual_BackEnd.dto.request.RegisterRequest;
import com.BarbiereDev.cardapio_virtual_BackEnd.dto.response.AuthResponse;
import com.BarbiereDev.cardapio_virtual_BackEnd.model.Role;
import com.BarbiereDev.cardapio_virtual_BackEnd.model.Usuario;
import com.BarbiereDev.cardapio_virtual_BackEnd.repository.UsuarioRepository;
import com.BarbiereDev.cardapio_virtual_BackEnd.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .nome("João Silva")
                .email("joao@email.com")
                .senha("123456")
                .role(Role.ADMIN)
                .build();

        loginRequest = LoginRequest.builder()
                .email("joao@email.com")
                .senha("123456")
                .build();

        usuario = Usuario.builder()
                .id(1L)
                .nome("João Silva")
                .email("joao@email.com")
                .senha("senha-hashada")
                .role(Role.ADMIN)
                .build();
    }

    // ----- REGISTER -----

    @Test
    @DisplayName("Deve registrar um novo usuário com sucesso")
    void registerSuccess() {
        when(usuarioRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getSenha())).thenReturn("senha-hashada");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        when(jwtService.generateToken(usuario.getEmail())).thenReturn("token-jwt");

        AuthResponse response = authService.register(registerRequest);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("token-jwt");
        assertThat(response.getNome()).isEqualTo("João Silva");
        assertThat(response.getEmail()).isEqualTo("joao@email.com");
        assertThat(response.getRole()).isEqualTo(Role.ADMIN);

        verify(usuarioRepository).existsByEmail("joao@email.com");
        verify(passwordEncoder).encode("123456");
        verify(usuarioRepository).save(any(Usuario.class));
        verify(jwtService).generateToken("joao@email.com");
    }

    @Test
    @DisplayName("Deve lançar exceção ao registrar com email duplicado")
    void registerWithDuplicateEmail() {
        when(usuarioRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Email já cadastrado");

        verify(usuarioRepository).existsByEmail("joao@email.com");
        verify(usuarioRepository, never()).save(any());
    }

    // ----- LOGIN -----

    @Test
    @DisplayName("Deve autenticar usuário com sucesso")
    void loginSuccess() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(usuario);
        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);
        when(jwtService.generateToken(usuario.getEmail())).thenReturn("token-jwt");

        AuthResponse response = authService.login(loginRequest);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("token-jwt");
        assertThat(response.getEmail()).isEqualTo("joao@email.com");

        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken("joao@email.com", "123456")
        );
        verify(usuarioRepository, never()).findByEmail(any());
        verify(jwtService).generateToken("joao@email.com");
    }
}
