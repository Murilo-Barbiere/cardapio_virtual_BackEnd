package com.BarbiereDev.cardapio_virtual_BackEnd.service;

import com.BarbiereDev.cardapio_virtual_BackEnd.dto.request.CardapioRequest;
import com.BarbiereDev.cardapio_virtual_BackEnd.dto.response.CardapioResponse;
import com.BarbiereDev.cardapio_virtual_BackEnd.model.Cardapio;
import com.BarbiereDev.cardapio_virtual_BackEnd.model.Estabelecimento;
import com.BarbiereDev.cardapio_virtual_BackEnd.model.Role;
import com.BarbiereDev.cardapio_virtual_BackEnd.model.Usuario;
import com.BarbiereDev.cardapio_virtual_BackEnd.repository.CardapioRepository;
import com.BarbiereDev.cardapio_virtual_BackEnd.repository.EstabelecimentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardapioServiceTest {

    @Mock
    private CardapioRepository cardapioRepository;

    @Mock
    private EstabelecimentoRepository estabelecimentoRepository;

    @InjectMocks
    private CardapioService cardapioService;

    private Usuario criador;
    private Usuario outroUsuario;
    private Estabelecimento estabelecimento;
    private Cardapio cardapio;
    private CardapioRequest request;

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

        cardapio = Cardapio.builder()
                .id(1L)
                .nome("Cardápio do Dia")
                .descricao("Pratos especiais do dia")
                .estabelecimento(estabelecimento)
                .build();

        request = CardapioRequest.builder()
                .nome("Cardápio do Dia")
                .descricao("Pratos especiais do dia")
                .build();
    }

    // ----- CREATE -----

    @Test
    @DisplayName("Deve criar cardapio quando for o criador do estabelecimento")
    void createByCriador() {
        when(estabelecimentoRepository.findById(1L)).thenReturn(Optional.of(estabelecimento));
        when(cardapioRepository.save(any(Cardapio.class))).thenReturn(cardapio);

        CardapioResponse resultado = cardapioService.create(1L, request, criador);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNome()).isEqualTo("Cardápio do Dia");
        assertThat(resultado.getDescricao()).isEqualTo("Pratos especiais do dia");
        verify(cardapioRepository).save(any(Cardapio.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar cardapio quando não for o criador")
    void createByNonCriador() {
        when(estabelecimentoRepository.findById(1L)).thenReturn(Optional.of(estabelecimento));

        assertThatThrownBy(() -> cardapioService.create(1L, request, outroUsuario))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Apenas o criador");

        verify(cardapioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar cardapio em estabelecimento inexistente")
    void createEstabelecimentoNotFound() {
        when(estabelecimentoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cardapioService.create(99L, request, criador))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("não encontrado");

        verify(cardapioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve criar cardapio sem descricao")
    void createSemDescricao() {
        CardapioRequest semDescricao = CardapioRequest.builder()
                .nome("Cardápio de Bebidas")
                .build();

        Cardapio cardapioSemDescricao = Cardapio.builder()
                .id(2L)
                .nome("Cardápio de Bebidas")
                .estabelecimento(estabelecimento)
                .build();

        when(estabelecimentoRepository.findById(1L)).thenReturn(Optional.of(estabelecimento));
        when(cardapioRepository.save(any(Cardapio.class))).thenReturn(cardapioSemDescricao);

        CardapioResponse resultado = cardapioService.create(1L, semDescricao, criador);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNome()).isEqualTo("Cardápio de Bebidas");
        assertThat(resultado.getDescricao()).isNull();
        verify(cardapioRepository).save(any(Cardapio.class));
    }

    // ----- UPDATE -----

    @Test
    @DisplayName("Deve atualizar cardapio quando for o criador do estabelecimento")
    void updateByCriador() {
        when(estabelecimentoRepository.findById(1L)).thenReturn(Optional.of(estabelecimento));
        when(cardapioRepository.findById(1L)).thenReturn(Optional.of(cardapio));
        when(cardapioRepository.save(any(Cardapio.class))).thenReturn(cardapio);

        CardapioRequest updateRequest = CardapioRequest.builder()
                .nome("Cardápio da Noite")
                .descricao("Pratos especiais da noite")
                .build();

        CardapioResponse resultado = cardapioService.update(1L, 1L, updateRequest, criador);

        assertThat(resultado).isNotNull();
        verify(cardapioRepository).save(any(Cardapio.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar cardapio quando não for o criador")
    void updateByNonCriador() {
        when(estabelecimentoRepository.findById(1L)).thenReturn(Optional.of(estabelecimento));

        assertThatThrownBy(() -> cardapioService.update(1L, 1L, request, outroUsuario))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Apenas o criador");

        verify(cardapioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar cardapio de outro estabelecimento")
    void updateCardapioNaoPertenceAoEstabelecimento() {
        Estabelecimento outroEstabelecimento = Estabelecimento.builder()
                .id(2L)
                .nome("Outro Restaurante")
                .slug("outro-restaurante")
                .criador(criador)
                .colaboradores(new HashSet<>(Set.of(criador)))
                .build();

        when(estabelecimentoRepository.findById(2L)).thenReturn(Optional.of(outroEstabelecimento));
        when(cardapioRepository.findById(1L)).thenReturn(Optional.of(cardapio));

        assertThatThrownBy(() -> cardapioService.update(2L, 1L, request, criador))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("não pertence a este estabelecimento");

        verify(cardapioRepository, never()).save(any());
    }

    // ----- DELETE -----

    @Test
    @DisplayName("Deve deletar cardapio quando for o criador do estabelecimento")
    void deleteByCriador() {
        when(estabelecimentoRepository.findById(1L)).thenReturn(Optional.of(estabelecimento));
        when(cardapioRepository.findById(1L)).thenReturn(Optional.of(cardapio));

        cardapioService.delete(1L, 1L, criador);

        verify(cardapioRepository).delete(cardapio);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar cardapio quando não for o criador")
    void deleteByNonCriador() {
        when(estabelecimentoRepository.findById(1L)).thenReturn(Optional.of(estabelecimento));

        assertThatThrownBy(() -> cardapioService.delete(1L, 1L, outroUsuario))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Apenas o criador");

        verify(cardapioRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar cardapio inexistente")
    void deleteCardapioNotFound() {
        when(estabelecimentoRepository.findById(1L)).thenReturn(Optional.of(estabelecimento));
        when(cardapioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cardapioService.delete(1L, 99L, criador))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("não encontrado");

        verify(cardapioRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar cardapio de outro estabelecimento")
    void deleteCardapioNaoPertenceAoEstabelecimento() {
        Estabelecimento outroEstabelecimento = Estabelecimento.builder()
                .id(2L)
                .nome("Outro Restaurante")
                .slug("outro-restaurante")
                .criador(criador)
                .colaboradores(new HashSet<>(Set.of(criador)))
                .build();

        when(estabelecimentoRepository.findById(2L)).thenReturn(Optional.of(outroEstabelecimento));
        when(cardapioRepository.findById(1L)).thenReturn(Optional.of(cardapio));

        assertThatThrownBy(() -> cardapioService.delete(2L, 1L, criador))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("não pertence a este estabelecimento");

        verify(cardapioRepository, never()).delete(any());
    }
}
