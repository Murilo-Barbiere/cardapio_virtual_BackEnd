package com.BarbiereDev.cardapio_virtual_BackEnd.service;

import com.BarbiereDev.cardapio_virtual_BackEnd.dto.request.LinkRequest;
import com.BarbiereDev.cardapio_virtual_BackEnd.dto.response.LinkResponse;
import com.BarbiereDev.cardapio_virtual_BackEnd.model.Estabelecimento;
import com.BarbiereDev.cardapio_virtual_BackEnd.model.Link;
import com.BarbiereDev.cardapio_virtual_BackEnd.model.Role;
import com.BarbiereDev.cardapio_virtual_BackEnd.model.Usuario;
import com.BarbiereDev.cardapio_virtual_BackEnd.repository.EstabelecimentoRepository;
import com.BarbiereDev.cardapio_virtual_BackEnd.repository.LinkRepository;
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
class LinkServiceTest {

    @Mock
    private LinkRepository linkRepository;

    @Mock
    private EstabelecimentoRepository estabelecimentoRepository;

    @InjectMocks
    private LinkService linkService;

    private Usuario criador;
    private Usuario outroUsuario;
    private Estabelecimento estabelecimento;
    private Link link;
    private LinkRequest request;

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

        link = Link.builder()
                .id(1L)
                .url("https://instagram.com/meurestaurante")
                .descricao("Instagram")
                .estabelecimento(estabelecimento)
                .build();

        request = LinkRequest.builder()
                .url("https://instagram.com/meurestaurante")
                .descricao("Instagram")
                .build();
    }

    // ----- CREATE -----

    @Test
    @DisplayName("Deve criar link quando for o criador do estabelecimento")
    void createByCriador() {
        when(estabelecimentoRepository.findById(1L)).thenReturn(Optional.of(estabelecimento));
        when(linkRepository.save(any(Link.class))).thenReturn(link);

        LinkResponse resultado = linkService.create(1L, request, criador);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getUrl()).isEqualTo("https://instagram.com/meurestaurante");
        assertThat(resultado.getDescricao()).isEqualTo("Instagram");
        verify(linkRepository).save(any(Link.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar link quando não for o criador")
    void createByNonCriador() {
        when(estabelecimentoRepository.findById(1L)).thenReturn(Optional.of(estabelecimento));

        assertThatThrownBy(() -> linkService.create(1L, request, outroUsuario))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Apenas o criador");

        verify(linkRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar link em estabelecimento inexistente")
    void createEstabelecimentoNotFound() {
        when(estabelecimentoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> linkService.create(99L, request, criador))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("não encontrado");

        verify(linkRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve criar link sem descricao")
    void createSemDescricao() {
        LinkRequest semDescricao = LinkRequest.builder()
                .url("https://facebook.com/meurestaurante")
                .build();

        Link linkSemDescricao = Link.builder()
                .id(2L)
                .url("https://facebook.com/meurestaurante")
                .estabelecimento(estabelecimento)
                .build();

        when(estabelecimentoRepository.findById(1L)).thenReturn(Optional.of(estabelecimento));
        when(linkRepository.save(any(Link.class))).thenReturn(linkSemDescricao);

        LinkResponse resultado = linkService.create(1L, semDescricao, criador);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getUrl()).isEqualTo("https://facebook.com/meurestaurante");
        assertThat(resultado.getDescricao()).isNull();
        verify(linkRepository).save(any(Link.class));
    }

    // ----- UPDATE -----

    @Test
    @DisplayName("Deve atualizar link quando for o criador do estabelecimento")
    void updateByCriador() {
        when(estabelecimentoRepository.findById(1L)).thenReturn(Optional.of(estabelecimento));
        when(linkRepository.findById(1L)).thenReturn(Optional.of(link));
        when(linkRepository.save(any(Link.class))).thenReturn(link);

        LinkRequest updateRequest = LinkRequest.builder()
                .url("https://instagram.com/novo")
                .descricao("Novo Instagram")
                .build();

        LinkResponse resultado = linkService.update(1L, 1L, updateRequest, criador);

        assertThat(resultado).isNotNull();
        verify(linkRepository).save(any(Link.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar link quando não for o criador")
    void updateByNonCriador() {
        when(estabelecimentoRepository.findById(1L)).thenReturn(Optional.of(estabelecimento));

        assertThatThrownBy(() -> linkService.update(1L, 1L, request, outroUsuario))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Apenas o criador");

        verify(linkRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar link de outro estabelecimento")
    void updateLinkNaoPertenceAoEstabelecimento() {
        Estabelecimento outroEstabelecimento = Estabelecimento.builder()
                .id(2L)
                .nome("Outro Restaurante")
                .slug("outro-restaurante")
                .criador(criador)
                .colaboradores(new HashSet<>(Set.of(criador)))
                .build();

        when(estabelecimentoRepository.findById(2L)).thenReturn(Optional.of(outroEstabelecimento));
        when(linkRepository.findById(1L)).thenReturn(Optional.of(link));

        assertThatThrownBy(() -> linkService.update(2L, 1L, request, criador))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("não pertence a este estabelecimento");

        verify(linkRepository, never()).save(any());
    }

    // ----- DELETE -----

    @Test
    @DisplayName("Deve deletar link quando for o criador do estabelecimento")
    void deleteByCriador() {
        when(estabelecimentoRepository.findById(1L)).thenReturn(Optional.of(estabelecimento));
        when(linkRepository.findById(1L)).thenReturn(Optional.of(link));

        linkService.delete(1L, 1L, criador);

        verify(linkRepository).delete(link);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar link quando não for o criador")
    void deleteByNonCriador() {
        when(estabelecimentoRepository.findById(1L)).thenReturn(Optional.of(estabelecimento));

        assertThatThrownBy(() -> linkService.delete(1L, 1L, outroUsuario))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Apenas o criador");

        verify(linkRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar link inexistente")
    void deleteLinkNotFound() {
        when(estabelecimentoRepository.findById(1L)).thenReturn(Optional.of(estabelecimento));
        when(linkRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> linkService.delete(1L, 99L, criador))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("não encontrado");

        verify(linkRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar link de outro estabelecimento")
    void deleteLinkNaoPertenceAoEstabelecimento() {
        Estabelecimento outroEstabelecimento = Estabelecimento.builder()
                .id(2L)
                .nome("Outro Restaurante")
                .slug("outro-restaurante")
                .criador(criador)
                .colaboradores(new HashSet<>(Set.of(criador)))
                .build();

        when(estabelecimentoRepository.findById(2L)).thenReturn(Optional.of(outroEstabelecimento));
        when(linkRepository.findById(1L)).thenReturn(Optional.of(link));

        assertThatThrownBy(() -> linkService.delete(2L, 1L, criador))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("não pertence a este estabelecimento");

        verify(linkRepository, never()).delete(any());
    }
}
