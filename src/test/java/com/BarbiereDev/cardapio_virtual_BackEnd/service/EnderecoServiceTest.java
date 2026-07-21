package com.BarbiereDev.cardapio_virtual_BackEnd.service;

import com.BarbiereDev.cardapio_virtual_BackEnd.dto.request.EnderecoRequest;
import com.BarbiereDev.cardapio_virtual_BackEnd.dto.response.EnderecoResponse;
import com.BarbiereDev.cardapio_virtual_BackEnd.model.Endereco;
import com.BarbiereDev.cardapio_virtual_BackEnd.model.Estabelecimento;
import com.BarbiereDev.cardapio_virtual_BackEnd.model.Role;
import com.BarbiereDev.cardapio_virtual_BackEnd.model.Usuario;
import com.BarbiereDev.cardapio_virtual_BackEnd.repository.EnderecoRepository;
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
class EnderecoServiceTest {

    @Mock
    private EnderecoRepository enderecoRepository;

    @Mock
    private EstabelecimentoRepository estabelecimentoRepository;

    @InjectMocks
    private EnderecoService enderecoService;

    private Usuario criador;
    private Usuario outroUsuario;
    private Estabelecimento estabelecimento;
    private Endereco endereco;
    private EnderecoRequest request;

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

        endereco = Endereco.builder()
                .id(1L)
                .cidade("São Paulo")
                .estado("SP")
                .bairro("Centro")
                .rua("Rua das Flores")
                .numero("123")
                .estabelecimento(estabelecimento)
                .build();

        request = EnderecoRequest.builder()
                .cidade("São Paulo")
                .estado("SP")
                .bairro("Centro")
                .rua("Rua das Flores")
                .numero("123")
                .build();
    }

    // ----- CREATE -----

    @Test
    @DisplayName("Deve criar endereco quando for o criador do estabelecimento")
    void createByCriador() {
        when(estabelecimentoRepository.findById(1L)).thenReturn(Optional.of(estabelecimento));
        when(enderecoRepository.existsByEstabelecimentoIdAndCidadeAndEstadoAndBairroAndRuaAndNumero(
                anyLong(), anyString(), anyString(), anyString(), anyString(), anyString()
        )).thenReturn(false);
        when(enderecoRepository.save(any(Endereco.class))).thenReturn(endereco);

        EnderecoResponse resultado = enderecoService.create(1L, request, criador);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getCidade()).isEqualTo("São Paulo");
        assertThat(resultado.getEstado()).isEqualTo("SP");
        assertThat(resultado.getBairro()).isEqualTo("Centro");
        assertThat(resultado.getRua()).isEqualTo("Rua das Flores");
        assertThat(resultado.getNumero()).isEqualTo("123");

        verify(enderecoRepository).save(any(Endereco.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar endereco quando não for o criador")
    void createByNonCriador() {
        when(estabelecimentoRepository.findById(1L)).thenReturn(Optional.of(estabelecimento));

        assertThatThrownBy(() -> enderecoService.create(1L, request, outroUsuario))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Apenas o criador");

        verify(enderecoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar endereco em estabelecimento inexistente")
    void createEstabelecimentoNotFound() {
        when(estabelecimentoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> enderecoService.create(99L, request, criador))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("não encontrado");

        verify(enderecoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar endereco duplicado no mesmo estabelecimento")
    void createEnderecoDuplicado() {
        when(estabelecimentoRepository.findById(1L)).thenReturn(Optional.of(estabelecimento));
        when(enderecoRepository.existsByEstabelecimentoIdAndCidadeAndEstadoAndBairroAndRuaAndNumero(
                anyLong(), anyString(), anyString(), anyString(), anyString(), anyString()
        )).thenReturn(true);

        assertThatThrownBy(() -> enderecoService.create(1L, request, criador))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Endereço já cadastrado");

        verify(enderecoRepository, never()).save(any());
    }

    // ----- UPDATE -----

    @Test
    @DisplayName("Deve atualizar endereco quando for o criador do estabelecimento")
    void updateByCriador() {
        when(estabelecimentoRepository.findById(1L)).thenReturn(Optional.of(estabelecimento));
        when(enderecoRepository.findById(1L)).thenReturn(Optional.of(endereco));
        when(enderecoRepository.save(any(Endereco.class))).thenReturn(endereco);

        EnderecoRequest updateRequest = EnderecoRequest.builder()
                .cidade("Rio de Janeiro")
                .estado("RJ")
                .bairro("Copacabana")
                .rua("Av Atlântica")
                .numero("500")
                .build();

        EnderecoResponse resultado = enderecoService.update(1L, 1L, updateRequest, criador);

        assertThat(resultado).isNotNull();
        verify(enderecoRepository).save(any(Endereco.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar endereco para um que já existe no estabelecimento")
    void updateEnderecoDuplicado() {
        when(estabelecimentoRepository.findById(1L)).thenReturn(Optional.of(estabelecimento));
        when(enderecoRepository.findById(1L)).thenReturn(Optional.of(endereco));

        EnderecoRequest updateRequest = EnderecoRequest.builder()
                .cidade("Rio de Janeiro")
                .estado("RJ")
                .bairro("Copacabana")
                .rua("Av Atlântica")
                .numero("500")
                .build();

        when(enderecoRepository.existsByEstabelecimentoIdAndCidadeAndEstadoAndBairroAndRuaAndNumero(
                anyLong(), anyString(), anyString(), anyString(), anyString(), anyString()
        )).thenReturn(true);

        assertThatThrownBy(() -> enderecoService.update(1L, 1L, updateRequest, criador))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Endereço já cadastrado");

        verify(enderecoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve permitir atualizar endereco mantendo os mesmos valores (não deve acusar duplicidade)")
    void updateEnderecoMantendoMesmosValores() {
        when(estabelecimentoRepository.findById(1L)).thenReturn(Optional.of(estabelecimento));
        when(enderecoRepository.findById(1L)).thenReturn(Optional.of(endereco));
        when(enderecoRepository.save(any(Endereco.class))).thenReturn(endereco);

        EnderecoResponse resultado = enderecoService.update(1L, 1L, request, criador);

        assertThat(resultado).isNotNull();
        verify(enderecoRepository, never()).existsByEstabelecimentoIdAndCidadeAndEstadoAndBairroAndRuaAndNumero(
                anyLong(), anyString(), anyString(), anyString(), anyString(), anyString()
        );
        verify(enderecoRepository).save(any(Endereco.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar endereco quando não for o criador")
    void updateByNonCriador() {
        when(estabelecimentoRepository.findById(1L)).thenReturn(Optional.of(estabelecimento));

        assertThatThrownBy(() -> enderecoService.update(1L, 1L, request, outroUsuario))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Apenas o criador");

        verify(enderecoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar endereco de outro estabelecimento")
    void updateEnderecoNaoPertenceAoEstabelecimento() {
        Estabelecimento outroEstabelecimento = Estabelecimento.builder()
                .id(2L)
                .nome("Outro Restaurante")
                .slug("outro-restaurante")
                .criador(criador)
                .colaboradores(new HashSet<>(Set.of(criador)))
                .build();

        when(estabelecimentoRepository.findById(2L)).thenReturn(Optional.of(outroEstabelecimento));
        when(enderecoRepository.findById(1L)).thenReturn(Optional.of(endereco));

        assertThatThrownBy(() -> enderecoService.update(2L, 1L, request, criador))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("não pertence a este estabelecimento");

        verify(enderecoRepository, never()).save(any());
    }

    // ----- DELETE -----

    @Test
    @DisplayName("Deve deletar endereco quando for o criador do estabelecimento")
    void deleteByCriador() {
        when(estabelecimentoRepository.findById(1L)).thenReturn(Optional.of(estabelecimento));
        when(enderecoRepository.findById(1L)).thenReturn(Optional.of(endereco));

        enderecoService.delete(1L, 1L, criador);

        verify(enderecoRepository).delete(endereco);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar endereco quando não for o criador")
    void deleteByNonCriador() {
        when(estabelecimentoRepository.findById(1L)).thenReturn(Optional.of(estabelecimento));

        assertThatThrownBy(() -> enderecoService.delete(1L, 1L, outroUsuario))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Apenas o criador");

        verify(enderecoRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar endereco inexistente")
    void deleteEnderecoNotFound() {
        when(estabelecimentoRepository.findById(1L)).thenReturn(Optional.of(estabelecimento));
        when(enderecoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> enderecoService.delete(1L, 99L, criador))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("não encontrado");

        verify(enderecoRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar endereco de outro estabelecimento")
    void deleteEnderecoNaoPertenceAoEstabelecimento() {
        Estabelecimento outroEstabelecimento = Estabelecimento.builder()
                .id(2L)
                .nome("Outro Restaurante")
                .slug("outro-restaurante")
                .criador(criador)
                .colaboradores(new HashSet<>(Set.of(criador)))
                .build();

        when(estabelecimentoRepository.findById(2L)).thenReturn(Optional.of(outroEstabelecimento));
        when(enderecoRepository.findById(1L)).thenReturn(Optional.of(endereco));

        assertThatThrownBy(() -> enderecoService.delete(2L, 1L, criador))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("não pertence a este estabelecimento");

        verify(enderecoRepository, never()).delete(any());
    }
}
