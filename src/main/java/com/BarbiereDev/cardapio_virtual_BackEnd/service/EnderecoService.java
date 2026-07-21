package com.BarbiereDev.cardapio_virtual_BackEnd.service;

import com.BarbiereDev.cardapio_virtual_BackEnd.dto.request.EnderecoRequest;
import com.BarbiereDev.cardapio_virtual_BackEnd.dto.response.EnderecoResponse;
import com.BarbiereDev.cardapio_virtual_BackEnd.model.Endereco;
import com.BarbiereDev.cardapio_virtual_BackEnd.model.Estabelecimento;
import com.BarbiereDev.cardapio_virtual_BackEnd.model.Usuario;
import com.BarbiereDev.cardapio_virtual_BackEnd.repository.EnderecoRepository;
import com.BarbiereDev.cardapio_virtual_BackEnd.repository.EstabelecimentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class EnderecoService {

    private final EnderecoRepository enderecoRepository;
    private final EstabelecimentoRepository estabelecimentoRepository;

    @Transactional
    public EnderecoResponse create(Long estabelecimentoId, EnderecoRequest request, Usuario usuarioLogado) {
        Estabelecimento estabelecimento = estabelecimentoRepository.findById(estabelecimentoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estabelecimento não encontrado"));

        if (!estabelecimento.getCriador().getId().equals(usuarioLogado.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas o criador pode gerenciar endereços");
        }

        verificarEnderecoDuplicado(estabelecimentoId, request);

        Endereco endereco = Endereco.builder()
                .cidade(request.getCidade())
                .estado(request.getEstado())
                .bairro(request.getBairro())
                .rua(request.getRua())
                .numero(request.getNumero())
                .estabelecimento(estabelecimento)
                .build();

        enderecoRepository.save(endereco);
        return toResponse(endereco);
    }

    @Transactional
    public EnderecoResponse update(Long estabelecimentoId, Long enderecoId, EnderecoRequest request, Usuario usuarioLogado) {
        Estabelecimento estabelecimento = estabelecimentoRepository.findById(estabelecimentoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estabelecimento não encontrado"));

        if (!estabelecimento.getCriador().getId().equals(usuarioLogado.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas o criador pode gerenciar endereços");
        }

        Endereco endereco = enderecoRepository.findById(enderecoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Endereço não encontrado"));

        if (!endereco.getEstabelecimento().getId().equals(estabelecimentoId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Endereço não pertence a este estabelecimento");
        }

        boolean isMesmoEndereco = endereco.getCidade().equals(request.getCidade())
                && endereco.getEstado().equals(request.getEstado())
                && endereco.getBairro().equals(request.getBairro())
                && endereco.getRua().equals(request.getRua())
                && endereco.getNumero().equals(request.getNumero());

        if (!isMesmoEndereco) {
            verificarEnderecoDuplicado(estabelecimentoId, request);
        }

        endereco.setCidade(request.getCidade());
        endereco.setEstado(request.getEstado());
        endereco.setBairro(request.getBairro());
        endereco.setRua(request.getRua());
        endereco.setNumero(request.getNumero());

        enderecoRepository.save(endereco);
        return toResponse(endereco);
    }

    @Transactional
    public void delete(Long estabelecimentoId, Long enderecoId, Usuario usuarioLogado) {
        Estabelecimento estabelecimento = estabelecimentoRepository.findById(estabelecimentoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estabelecimento não encontrado"));

        if (!estabelecimento.getCriador().getId().equals(usuarioLogado.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas o criador pode gerenciar endereços");
        }

        Endereco endereco = enderecoRepository.findById(enderecoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Endereço não encontrado"));

        if (!endereco.getEstabelecimento().getId().equals(estabelecimentoId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Endereço não pertence a este estabelecimento");
        }

        enderecoRepository.delete(endereco);
    }

    private void verificarEnderecoDuplicado(Long estabelecimentoId, EnderecoRequest request) {
        boolean duplicado = enderecoRepository.existsByEstabelecimentoIdAndCidadeAndEstadoAndBairroAndRuaAndNumero(
                estabelecimentoId,
                request.getCidade(),
                request.getEstado(),
                request.getBairro(),
                request.getRua(),
                request.getNumero()
        );

        if (duplicado) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Endereço já cadastrado para este estabelecimento");
        }
    }

    public EnderecoResponse toResponse(Endereco endereco) {
        return EnderecoResponse.builder()
                .id(endereco.getId())
                .cidade(endereco.getCidade())
                .estado(endereco.getEstado())
                .bairro(endereco.getBairro())
                .rua(endereco.getRua())
                .numero(endereco.getNumero())
                .createdAt(endereco.getCreatedAt())
                .updatedAt(endereco.getUpdatedAt())
                .build();
    }
}
