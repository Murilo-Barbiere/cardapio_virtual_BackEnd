package com.BarbiereDev.cardapio_virtual_BackEnd.service;

import com.BarbiereDev.cardapio_virtual_BackEnd.dto.request.UsuarioUpdateRequest;
import com.BarbiereDev.cardapio_virtual_BackEnd.dto.response.UsuarioResponse;
import com.BarbiereDev.cardapio_virtual_BackEnd.model.Role;
import com.BarbiereDev.cardapio_virtual_BackEnd.model.Usuario;
import com.BarbiereDev.cardapio_virtual_BackEnd.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;
    private UsuarioUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .id(1L)
                .nome("João Silva")
                .email("joao@email.com")
                .senha("senha-hashada")
                .role(Role.ADMIN)
                .build();

        updateRequest = UsuarioUpdateRequest.builder()
                .nome("João Atualizado")
                .email("joao.novo@email.com")
                .role(Role.COLABORADOR)
                .build();
    }

    // ----- FIND ALL -----

    @Test
    @DisplayName("Deve listar todos os usuários")
    void findAll() {
        when(usuarioRepository.findAll()).thenReturn(List.of(usuario));

        var resultado = usuarioService.findAll();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNome()).isEqualTo("João Silva");
        verify(usuarioRepository).findAll();
    }

    // ----- FIND BY ID -----

    @Test
    @DisplayName("Deve retornar usuário por ID com sucesso")
    void findByIdSuccess() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        var resultado = usuarioService.findById(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNome()).isEqualTo("João Silva");
        assertThat(resultado.getEmail()).isEqualTo("joao@email.com");
        verify(usuarioRepository).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar usuário inexistente")
    void findByIdNotFound() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.findById(99L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Usuário não encontrado");
    }

    // ----- UPDATE -----

    @Test
    @DisplayName("Deve atualizar usuário com sucesso")
    void updateSuccess() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.existsByEmail("joao.novo@email.com")).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        var resultado = usuarioService.update(1L, updateRequest);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNome()).isEqualTo("João Atualizado");

        verify(usuarioRepository).findById(1L);
        verify(usuarioRepository).existsByEmail("joao.novo@email.com");
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar com email já existente")
    void updateWithDuplicateEmail() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.existsByEmail("joao.novo@email.com")).thenReturn(true);

        assertThatThrownBy(() -> usuarioService.update(1L, updateRequest))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Email já cadastrado");

        verify(usuarioRepository).findById(1L);
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar usuário inexistente")
    void updateNotFound() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.update(99L, updateRequest))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Usuário não encontrado");
    }

    // ----- DELETE -----

    @Test
    @DisplayName("Deve deletar usuário com sucesso")
    void deleteSuccess() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);

        usuarioService.delete(1L);

        verify(usuarioRepository).existsById(1L);
        verify(usuarioRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar usuário inexistente")
    void deleteNotFound() {
        when(usuarioRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> usuarioService.delete(99L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Usuário não encontrado");

        verify(usuarioRepository, never()).deleteById(any());
    }

    // ----- TO RESPONSE -----

    @Test
    @DisplayName("Deve converter Usuario para UsuarioResponse sem senha")
    void toResponse() {
        var response = usuarioService.toResponse(usuario);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getNome()).isEqualTo("João Silva");
        assertThat(response.getEmail()).isEqualTo("joao@email.com");
        assertThat(response.getRole()).isEqualTo(Role.ADMIN);
        // Senha NÃO deve estar na resposta
        assertThat(response.getClass().getDeclaredFields())
                .extracting(java.lang.reflect.Field::getName)
                .doesNotContain("senha");
    }
}
