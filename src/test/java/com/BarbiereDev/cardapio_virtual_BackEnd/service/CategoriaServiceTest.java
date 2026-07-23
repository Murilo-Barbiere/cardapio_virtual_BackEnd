package com.BarbiereDev.cardapio_virtual_BackEnd.service;

import com.BarbiereDev.cardapio_virtual_BackEnd.dto.request.CategoriaRequest;
import com.BarbiereDev.cardapio_virtual_BackEnd.dto.response.CategoriaResponse;
import com.BarbiereDev.cardapio_virtual_BackEnd.model.Cardapio;
import com.BarbiereDev.cardapio_virtual_BackEnd.model.Categoria;
import com.BarbiereDev.cardapio_virtual_BackEnd.model.Estabelecimento;
import com.BarbiereDev.cardapio_virtual_BackEnd.model.Role;
import com.BarbiereDev.cardapio_virtual_BackEnd.model.Usuario;
import com.BarbiereDev.cardapio_virtual_BackEnd.repository.CardapioRepository;
import com.BarbiereDev.cardapio_virtual_BackEnd.repository.CategoriaRepository;
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
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private CardapioRepository cardapioRepository;

    @Mock
    private EstabelecimentoRepository estabelecimentoRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    private Usuario criador;
    private Usuario outroUsuario;
    private Estabelecimento estabelecimento;
    private Cardapio cardapio;
    private Categoria categoria;
    private CategoriaRequest request;

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
                .estabelecimento(estabelecimento)
                .build();

        categoria = Categoria.builder()
                .id(1L)
                .nome("Bebidas")
                .descricao("Bebidas em geral")
                .ordemExibicao(1)
                .cardapio(cardapio)
                .build();

        request = CategoriaRequest.builder()
                .nome("Bebidas")
                .descricao("Bebidas em geral")
                .ordemExibicao(1)
                .build();
    }

    @Test
    @DisplayName("Deve criar categoria quando for o criador do estabelecimento")
    void createByCriador() {
        when(estabelecimentoRepository.findById(1L)).thenReturn(Optional.of(estabelecimento));
        when(cardapioRepository.findById(1L)).thenReturn(Optional.of(cardapio));
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoria);

        CategoriaResponse resultado = categoriaService.create(1L, 1L, request, criador);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNome()).isEqualTo("Bebidas");
        assertThat(resultado.getDescricao()).isEqualTo("Bebidas em geral");
        assertThat(resultado.getOrdemExibicao()).isEqualTo(1);
        verify(categoriaRepository).save(any(Categoria.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar categoria quando não for o criador")
    void createByNonCriador() {
        when(estabelecimentoRepository.findById(1L)).thenReturn(Optional.of(estabelecimento));

        assertThatThrownBy(() -> categoriaService.create(1L, 1L, request, outroUsuario))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Apenas o criador");

        verify(categoriaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar categoria em estabelecimento inexistente")
    void createEstabelecimentoNotFound() {
        when(estabelecimentoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoriaService.create(99L, 1L, request, criador))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("não encontrado");

        verify(categoriaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar categoria em cardapio inexistente")
    void createCardapioNotFound() {
        when(estabelecimentoRepository.findById(1L)).thenReturn(Optional.of(estabelecimento));
        when(cardapioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoriaService.create(1L, 99L, request, criador))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("não encontrado");

        verify(categoriaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve listar categorias do cardapio")
    void findAllByCardapio() {
        when(estabelecimentoRepository.existsById(1L)).thenReturn(true);
        when(cardapioRepository.findById(1L)).thenReturn(Optional.of(cardapio));
        when(categoriaRepository.findByCardapioIdOrderByOrdemExibicaoAscNomeAsc(1L)).thenReturn(List.of(categoria));

        List<CategoriaResponse> resultado = categoriaService.findAllByCardapio(1L, 1L);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNome()).isEqualTo("Bebidas");
    }

    @Test
    @DisplayName("Deve buscar categoria por ID")
    void findById() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        CategoriaResponse resultado = categoriaService.findById(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNome()).isEqualTo("Bebidas");
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar categoria inexistente")
    void findByIdNotFound() {
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoriaService.findById(99L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("não encontrada");
    }

    @Test
    @DisplayName("Deve atualizar categoria quando for o criador")
    void updateByCriador() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoria);

        CategoriaRequest updateRequest = CategoriaRequest.builder()
                .nome("Bebidas Atualizado")
                .descricao("Nova descrição")
                .ordemExibicao(2)
                .build();

        CategoriaResponse resultado = categoriaService.update(1L, updateRequest, criador);

        assertThat(resultado).isNotNull();
        verify(categoriaRepository).save(any(Categoria.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar categoria quando não for o criador")
    void updateByNonCriador() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        assertThatThrownBy(() -> categoriaService.update(1L, request, outroUsuario))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Apenas o criador");

        verify(categoriaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve deletar categoria quando for o criador")
    void deleteByCriador() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        categoriaService.delete(1L, criador);

        verify(categoriaRepository).delete(categoria);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar categoria quando não for o criador")
    void deleteByNonCriador() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        assertThatThrownBy(() -> categoriaService.delete(1L, outroUsuario))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Apenas o criador");

        verify(categoriaRepository, never()).delete(any());
    }
}
