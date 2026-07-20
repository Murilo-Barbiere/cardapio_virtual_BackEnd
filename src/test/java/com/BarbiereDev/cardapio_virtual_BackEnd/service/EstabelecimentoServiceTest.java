package com.BarbiereDev.cardapio_virtual_BackEnd.service;

import com.BarbiereDev.cardapio_virtual_BackEnd.dto.request.EstabelecimentoRequest;
import com.BarbiereDev.cardapio_virtual_BackEnd.dto.response.EstabelecimentoResponse;
import com.BarbiereDev.cardapio_virtual_BackEnd.model.Estabelecimento;
import com.BarbiereDev.cardapio_virtual_BackEnd.model.Role;
import com.BarbiereDev.cardapio_virtual_BackEnd.model.Usuario;
import com.BarbiereDev.cardapio_virtual_BackEnd.repository.EstabelecimentoRepository;
import com.BarbiereDev.cardapio_virtual_BackEnd.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EstabelecimentoServiceTest {

    @Mock
    private EstabelecimentoRepository estabelecimentoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private EstabelecimentoService estabelecimentoService;

    private Usuario criador;
    private Usuario outroUsuario;
    private Estabelecimento estabelecimento;
    private EstabelecimentoRequest request;

    @BeforeEach
    void setUp() {
        criador = Usuario.builder()
                .id(1L)
                .nome("João Dono")
                .email("joao@email.com")
                .senha("hash")
                .role(Role.ADMIN)
                .build();

        outroUsuario = Usuario.builder()
                .id(2L)
                .nome("Maria Colab")
                .email("maria@email.com")
                .senha("hash")
                .role(Role.COLABORADOR)
                .build();

        estabelecimento = Estabelecimento.builder()
                .id(1L)
                .nome("Restaurante do João")
                .slug("restaurante-do-joao")
                .criador(criador)
                .colaboradores(new HashSet<>(Set.of(criador)))
                .build();

        request = EstabelecimentoRequest.builder()
                .nome("Restaurante do João")
                .build();
    }

    // ----- FIND ALL -----

    @Test
    @DisplayName("Deve listar todos os estabelecimentos")
    void findAll() {
        when(estabelecimentoRepository.findAll()).thenReturn(List.of(estabelecimento));

        List<EstabelecimentoResponse> resultado = estabelecimentoService.findAll();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNome()).isEqualTo("Restaurante do João");
        verify(estabelecimentoRepository).findAll();
    }

    // ----- FIND BY ID -----

    @Test
    @DisplayName("Deve retornar estabelecimento por ID")
    void findByIdSuccess() {
        when(estabelecimentoRepository.findById(1L)).thenReturn(Optional.of(estabelecimento));

        EstabelecimentoResponse resultado = estabelecimentoService.findById(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNome()).isEqualTo("Restaurante do João");
        assertThat(resultado.getSlug()).isEqualTo("restaurante-do-joao");
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar estabelecimento inexistente")
    void findByIdNotFound() {
        when(estabelecimentoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> estabelecimentoService.findById(99L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("não encontrado");
    }

    // ----- FIND BY SLUG -----

    @Test
    @DisplayName("Deve retornar estabelecimento por slug")
    void findBySlugSuccess() {
        when(estabelecimentoRepository.findBySlug("restaurante-do-joao")).thenReturn(Optional.of(estabelecimento));

        EstabelecimentoResponse resultado = estabelecimentoService.findBySlug("restaurante-do-joao");

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNome()).isEqualTo("Restaurante do João");
    }

    // ----- CREATE -----

    @Test
    @DisplayName("Deve criar estabelecimento com slug gerado automaticamente")
    void createWithAutoSlug() {
        when(estabelecimentoRepository.existsBySlug(anyString())).thenReturn(false);
        when(estabelecimentoRepository.save(any(Estabelecimento.class))).thenReturn(estabelecimento);

        EstabelecimentoResponse resultado = estabelecimentoService.create(request, criador);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNome()).isEqualTo("Restaurante do João");
        assertThat(resultado.getCriador().getId()).isEqualTo(1L);
        assertThat(resultado.getColaboradores())
                .extracting(EstabelecimentoResponse.UsuarioResumo::getId)
                .contains(1L);

        verify(estabelecimentoRepository).save(any(Estabelecimento.class));
    }

    @Test
    @DisplayName("Deve criar estabelecimento com slug personalizado")
    void createWithCustomSlug() {
        request.setSlug("meu-restaurante");
        when(estabelecimentoRepository.existsBySlug("meu-restaurante")).thenReturn(false);
        when(estabelecimentoRepository.save(any(Estabelecimento.class))).thenReturn(estabelecimento);

        estabelecimentoService.create(request, criador);

        verify(estabelecimentoRepository).existsBySlug("meu-restaurante");
    }

    // ----- UPDATE -----

    @Test
    @DisplayName("Deve atualizar estabelecimento quando for o criador")
    void updateByCriador() {
        when(estabelecimentoRepository.findById(1L)).thenReturn(Optional.of(estabelecimento));
        when(estabelecimentoRepository.save(any(Estabelecimento.class))).thenReturn(estabelecimento);

        EstabelecimentoResponse resultado = estabelecimentoService.update(1L, request, criador);

        assertThat(resultado).isNotNull();
        verify(estabelecimentoRepository).save(any(Estabelecimento.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar quando não for o criador")
    void updateByNonCriador() {
        when(estabelecimentoRepository.findById(1L)).thenReturn(Optional.of(estabelecimento));

        assertThatThrownBy(() -> estabelecimentoService.update(1L, request, outroUsuario))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Apenas o criador");
    }

    // ----- DELETE -----

    @Test
    @DisplayName("Deve deletar estabelecimento quando for o criador")
    void deleteByCriador() {
        when(estabelecimentoRepository.findById(1L)).thenReturn(Optional.of(estabelecimento));

        estabelecimentoService.delete(1L, criador);

        verify(estabelecimentoRepository).delete(estabelecimento);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar quando não for o criador")
    void deleteByNonCriador() {
        when(estabelecimentoRepository.findById(1L)).thenReturn(Optional.of(estabelecimento));

        assertThatThrownBy(() -> estabelecimentoService.delete(1L, outroUsuario))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Apenas o criador");
    }

    // ----- COLABORADORES -----

    @Test
    @DisplayName("Deve adicionar colaborador quando for o criador")
    void adicionarColaborador() {
        when(estabelecimentoRepository.findById(1L)).thenReturn(Optional.of(estabelecimento));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(outroUsuario));
        when(estabelecimentoRepository.save(any(Estabelecimento.class))).thenReturn(estabelecimento);

        estabelecimentoService.adicionarColaborador(1L, 2L, criador);

        verify(estabelecimentoRepository).save(any(Estabelecimento.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao adicionar colaborador quando não for o criador")
    void adicionarColaboradorByNonCriador() {
        when(estabelecimentoRepository.findById(1L)).thenReturn(Optional.of(estabelecimento));

        assertThatThrownBy(() -> estabelecimentoService.adicionarColaborador(1L, 2L, outroUsuario))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Apenas o criador");
    }

    @Test
    @DisplayName("Deve remover colaborador quando for o criador")
    void removerColaborador() {
        estabelecimento.getColaboradores().add(outroUsuario);
        when(estabelecimentoRepository.findById(1L)).thenReturn(Optional.of(estabelecimento));
        when(estabelecimentoRepository.save(any(Estabelecimento.class))).thenReturn(estabelecimento);

        estabelecimentoService.removerColaborador(1L, 2L, criador);

        verify(estabelecimentoRepository).save(any(Estabelecimento.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao remover o criador como colaborador")
    void removerCriadorComoColaborador() {
        when(estabelecimentoRepository.findById(1L)).thenReturn(Optional.of(estabelecimento));

        assertThatThrownBy(() -> estabelecimentoService.removerColaborador(1L, 1L, criador))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Não é possível remover o criador");
    }
}
