package com.BarbiereDev.cardapio_virtual_BackEnd.service;

import com.BarbiereDev.cardapio_virtual_BackEnd.dto.request.ItemCardapioCreateRequest;
import com.BarbiereDev.cardapio_virtual_BackEnd.dto.request.ItemCardapioUpdateRequest;
import com.BarbiereDev.cardapio_virtual_BackEnd.dto.response.ItemCardapioResponse;
import com.BarbiereDev.cardapio_virtual_BackEnd.model.Cardapio;
import com.BarbiereDev.cardapio_virtual_BackEnd.model.Categoria;
import com.BarbiereDev.cardapio_virtual_BackEnd.model.Estabelecimento;
import com.BarbiereDev.cardapio_virtual_BackEnd.model.ItemCardapio;
import com.BarbiereDev.cardapio_virtual_BackEnd.model.Role;
import com.BarbiereDev.cardapio_virtual_BackEnd.model.Usuario;
import com.BarbiereDev.cardapio_virtual_BackEnd.repository.CategoriaRepository;
import com.BarbiereDev.cardapio_virtual_BackEnd.repository.ItemCardapioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemCardapioServiceTest {

    @Mock
    private ItemCardapioRepository itemCardapioRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private ItemCardapioService itemCardapioService;

    private Usuario criador;
    private Usuario outroUsuario;
    private Estabelecimento estabelecimento;
    private Cardapio cardapio;
    private Categoria categoria;
    private ItemCardapio item;
    private ItemCardapioCreateRequest createRequest;
    private ItemCardapioUpdateRequest updateRequest;

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
                .cardapio(cardapio)
                .build();

        item = ItemCardapio.builder()
                .id(1L)
                .nome("Coca-Cola")
                .descricao("Lata 350ml")
                .preco(new BigDecimal("5.00"))
                .disponivel(true)
                .destaque(false)
                .ordemExibicao(1)
                .categoria(categoria)
                .build();

        createRequest = ItemCardapioCreateRequest.builder()
                .nome("Coca-Cola")
                .descricao("Lata 350ml")
                .preco(new BigDecimal("5.00"))
                .disponivel(true)
                .destaque(false)
                .ordemExibicao(1)
                .build();

        updateRequest = ItemCardapioUpdateRequest.builder()
                .nome("Coca-Cola 2L")
                .descricao("Garrafa 2 litros")
                .preco(new BigDecimal("8.00"))
                .disponivel(true)
                .destaque(true)
                .ordemExibicao(2)
                .build();
    }

    @Test
    @DisplayName("Deve criar item quando for o criador do estabelecimento")
    void createByCriador() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(itemCardapioRepository.save(any(ItemCardapio.class))).thenReturn(item);

        ItemCardapioResponse resultado = itemCardapioService.create(1L, createRequest, criador);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNome()).isEqualTo("Coca-Cola");
        assertThat(resultado.getPreco()).isEqualByComparingTo(new BigDecimal("5.00"));
        assertThat(resultado.getDisponivel()).isTrue();
        assertThat(resultado.getDestaque()).isFalse();
        verify(itemCardapioRepository).save(any(ItemCardapio.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar item quando não for o criador")
    void createByNonCriador() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        assertThatThrownBy(() -> itemCardapioService.create(1L, createRequest, outroUsuario))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Apenas o criador");

        verify(itemCardapioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar item em categoria inexistente")
    void createCategoriaNotFound() {
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemCardapioService.create(99L, createRequest, criador))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("não encontrada");

        verify(itemCardapioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve criar item com valores padrao quando disponivel e destaque nao forem informados")
    void createComValoresPadrao() {
        ItemCardapioCreateRequest semOptionais = ItemCardapioCreateRequest.builder()
                .nome("Suco de Laranja")
                .preco(new BigDecimal("7.00"))
                .build();

        ItemCardapio itemSemOptionais = ItemCardapio.builder()
                .id(2L)
                .nome("Suco de Laranja")
                .preco(new BigDecimal("7.00"))
                .disponivel(true)
                .destaque(false)
                .categoria(categoria)
                .build();

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(itemCardapioRepository.save(any(ItemCardapio.class))).thenReturn(itemSemOptionais);

        ItemCardapioResponse resultado = itemCardapioService.create(1L, semOptionais, criador);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getDisponivel()).isTrue();
        assertThat(resultado.getDestaque()).isFalse();
    }

    @Test
    @DisplayName("Deve buscar item por ID")
    void findById() {
        when(itemCardapioRepository.findById(1L)).thenReturn(Optional.of(item));

        ItemCardapioResponse resultado = itemCardapioService.findById(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNome()).isEqualTo("Coca-Cola");
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar item inexistente")
    void findByIdNotFound() {
        when(itemCardapioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemCardapioService.findById(99L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("não encontrado");
    }

    @Test
    @DisplayName("Deve listar itens disponiveis de uma categoria por padrao")
    void findAllByCategoriaApenasDisponiveis() {
        when(categoriaRepository.existsById(1L)).thenReturn(true);
        when(itemCardapioRepository.findByCategoriaIdAndDisponivelTrueOrderByOrdemExibicaoAscNomeAsc(1L))
                .thenReturn(List.of(item));

        List<ItemCardapioResponse> resultado = itemCardapioService.findAllByCategoria(1L, true);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNome()).isEqualTo("Coca-Cola");
    }

    @Test
    @DisplayName("Deve listar todos os itens quando filtro disponivel for false")
    void findAllByCategoriaTodos() {
        when(categoriaRepository.existsById(1L)).thenReturn(true);
        when(itemCardapioRepository.findByCategoriaIdOrderByOrdemExibicaoAscNomeAsc(1L))
                .thenReturn(List.of(item));

        List<ItemCardapioResponse> resultado = itemCardapioService.findAllByCategoria(1L, false);

        assertThat(resultado).hasSize(1);
    }

    @Test
    @DisplayName("Deve atualizar item quando for o criador")
    void updateByCriador() {
        when(itemCardapioRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemCardapioRepository.save(any(ItemCardapio.class))).thenReturn(item);

        ItemCardapioResponse resultado = itemCardapioService.update(1L, updateRequest, criador);

        assertThat(resultado).isNotNull();
        verify(itemCardapioRepository).save(any(ItemCardapio.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar item quando não for o criador")
    void updateByNonCriador() {
        when(itemCardapioRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> itemCardapioService.update(1L, updateRequest, outroUsuario))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Apenas o criador");

        verify(itemCardapioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve deletar item quando for o criador")
    void deleteByCriador() {
        when(itemCardapioRepository.findById(1L)).thenReturn(Optional.of(item));

        itemCardapioService.delete(1L, criador);

        verify(itemCardapioRepository).delete(item);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar item quando não for o criador")
    void deleteByNonCriador() {
        when(itemCardapioRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> itemCardapioService.delete(1L, outroUsuario))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Apenas o criador");

        verify(itemCardapioRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Deve atualizar disponibilidade do item")
    void atualizarDisponibilidade() {
        when(itemCardapioRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemCardapioRepository.save(any(ItemCardapio.class))).thenReturn(item);

        ItemCardapioResponse resultado = itemCardapioService.atualizarDisponibilidade(1L, false, criador);

        assertThat(resultado).isNotNull();
        verify(itemCardapioRepository).save(any(ItemCardapio.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao alterar disponibilidade quando não for o criador")
    void atualizarDisponibilidadeByNonCriador() {
        when(itemCardapioRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> itemCardapioService.atualizarDisponibilidade(1L, false, outroUsuario))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Apenas o criador");

        verify(itemCardapioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve atualizar destaque do item")
    void atualizarDestaque() {
        when(itemCardapioRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemCardapioRepository.save(any(ItemCardapio.class))).thenReturn(item);

        ItemCardapioResponse resultado = itemCardapioService.atualizarDestaque(1L, true, criador);

        assertThat(resultado).isNotNull();
        verify(itemCardapioRepository).save(any(ItemCardapio.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao alterar destaque quando não for o criador")
    void atualizarDestaqueByNonCriador() {
        when(itemCardapioRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> itemCardapioService.atualizarDestaque(1L, true, outroUsuario))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Apenas o criador");

        verify(itemCardapioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve atualizar ordem de exibicao do item")
    void atualizarOrdem() {
        when(itemCardapioRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemCardapioRepository.save(any(ItemCardapio.class))).thenReturn(item);

        ItemCardapioResponse resultado = itemCardapioService.atualizarOrdem(1L, 5, criador);

        assertThat(resultado).isNotNull();
        verify(itemCardapioRepository).save(any(ItemCardapio.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao alterar ordem quando não for o criador")
    void atualizarOrdemByNonCriador() {
        when(itemCardapioRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> itemCardapioService.atualizarOrdem(1L, 5, outroUsuario))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Apenas o criador");

        verify(itemCardapioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar item inexistente")
    void updateItemNotFound() {
        when(itemCardapioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemCardapioService.update(99L, updateRequest, criador))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("não encontrado");

        verify(itemCardapioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar item inexistente")
    void deleteItemNotFound() {
        when(itemCardapioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemCardapioService.delete(99L, criador))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("não encontrado");

        verify(itemCardapioRepository, never()).delete(any());
    }
}
