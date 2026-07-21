package com.BarbiereDev.cardapio_virtual_BackEnd.service;

import com.BarbiereDev.cardapio_virtual_BackEnd.dto.request.CardapioRequest;
import com.BarbiereDev.cardapio_virtual_BackEnd.dto.response.CardapioResponse;
import com.BarbiereDev.cardapio_virtual_BackEnd.model.Cardapio;
import com.BarbiereDev.cardapio_virtual_BackEnd.model.Estabelecimento;
import com.BarbiereDev.cardapio_virtual_BackEnd.model.Usuario;
import com.BarbiereDev.cardapio_virtual_BackEnd.repository.CardapioRepository;
import com.BarbiereDev.cardapio_virtual_BackEnd.repository.EstabelecimentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CardapioService {

    private final CardapioRepository cardapioRepository;
    private final EstabelecimentoRepository estabelecimentoRepository;

    @Transactional
    public CardapioResponse create(Long estabelecimentoId, CardapioRequest request, Usuario usuarioLogado) {
        Estabelecimento estabelecimento = estabelecimentoRepository.findById(estabelecimentoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estabelecimento não encontrado"));

        if (!estabelecimento.getCriador().getId().equals(usuarioLogado.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas o criador pode gerenciar cardápios");
        }

        Cardapio cardapio = Cardapio.builder()
                .nome(request.getNome())
                .descricao(request.getDescricao())
                .estabelecimento(estabelecimento)
                .build();

        cardapioRepository.save(cardapio);
        return toResponse(cardapio);
    }

    @Transactional
    public CardapioResponse update(Long estabelecimentoId, Long cardapioId, CardapioRequest request, Usuario usuarioLogado) {
        Estabelecimento estabelecimento = estabelecimentoRepository.findById(estabelecimentoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estabelecimento não encontrado"));

        if (!estabelecimento.getCriador().getId().equals(usuarioLogado.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas o criador pode gerenciar cardápios");
        }

        Cardapio cardapio = cardapioRepository.findById(cardapioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cardápio não encontrado"));

        if (!cardapio.getEstabelecimento().getId().equals(estabelecimentoId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cardápio não pertence a este estabelecimento");
        }

        cardapio.setNome(request.getNome());
        cardapio.setDescricao(request.getDescricao());

        cardapioRepository.save(cardapio);
        return toResponse(cardapio);
    }

    @Transactional
    public void delete(Long estabelecimentoId, Long cardapioId, Usuario usuarioLogado) {
        Estabelecimento estabelecimento = estabelecimentoRepository.findById(estabelecimentoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estabelecimento não encontrado"));

        if (!estabelecimento.getCriador().getId().equals(usuarioLogado.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas o criador pode gerenciar cardápios");
        }

        Cardapio cardapio = cardapioRepository.findById(cardapioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cardápio não encontrado"));

        if (!cardapio.getEstabelecimento().getId().equals(estabelecimentoId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cardápio não pertence a este estabelecimento");
        }

        cardapioRepository.delete(cardapio);
    }

    public CardapioResponse toResponse(Cardapio cardapio) {
        return CardapioResponse.builder()
                .id(cardapio.getId())
                .nome(cardapio.getNome())
                .descricao(cardapio.getDescricao())
                .createdAt(cardapio.getCreatedAt())
                .updatedAt(cardapio.getUpdatedAt())
                .build();
    }
}
