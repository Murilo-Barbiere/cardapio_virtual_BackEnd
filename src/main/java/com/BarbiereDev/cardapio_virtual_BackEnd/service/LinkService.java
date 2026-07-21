package com.BarbiereDev.cardapio_virtual_BackEnd.service;

import com.BarbiereDev.cardapio_virtual_BackEnd.dto.request.LinkRequest;
import com.BarbiereDev.cardapio_virtual_BackEnd.dto.response.LinkResponse;
import com.BarbiereDev.cardapio_virtual_BackEnd.model.Estabelecimento;
import com.BarbiereDev.cardapio_virtual_BackEnd.model.Link;
import com.BarbiereDev.cardapio_virtual_BackEnd.model.Usuario;
import com.BarbiereDev.cardapio_virtual_BackEnd.repository.EstabelecimentoRepository;
import com.BarbiereDev.cardapio_virtual_BackEnd.repository.LinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class LinkService {

    private final LinkRepository linkRepository;
    private final EstabelecimentoRepository estabelecimentoRepository;

    @Transactional
    public LinkResponse create(Long estabelecimentoId, LinkRequest request, Usuario usuarioLogado) {
        Estabelecimento estabelecimento = estabelecimentoRepository.findById(estabelecimentoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estabelecimento não encontrado"));

        if (!estabelecimento.getCriador().getId().equals(usuarioLogado.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas o criador pode gerenciar links");
        }

        Link link = Link.builder()
                .url(request.getUrl())
                .descricao(request.getDescricao())
                .estabelecimento(estabelecimento)
                .build();

        linkRepository.save(link);
        return toResponse(link);
    }

    @Transactional
    public LinkResponse update(Long estabelecimentoId, Long linkId, LinkRequest request, Usuario usuarioLogado) {
        Estabelecimento estabelecimento = estabelecimentoRepository.findById(estabelecimentoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estabelecimento não encontrado"));

        if (!estabelecimento.getCriador().getId().equals(usuarioLogado.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas o criador pode gerenciar links");
        }

        Link link = linkRepository.findById(linkId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Link não encontrado"));

        if (!link.getEstabelecimento().getId().equals(estabelecimentoId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Link não pertence a este estabelecimento");
        }

        link.setUrl(request.getUrl());
        link.setDescricao(request.getDescricao());

        linkRepository.save(link);
        return toResponse(link);
    }

    @Transactional
    public void delete(Long estabelecimentoId, Long linkId, Usuario usuarioLogado) {
        Estabelecimento estabelecimento = estabelecimentoRepository.findById(estabelecimentoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estabelecimento não encontrado"));

        if (!estabelecimento.getCriador().getId().equals(usuarioLogado.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas o criador pode gerenciar links");
        }

        Link link = linkRepository.findById(linkId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Link não encontrado"));

        if (!link.getEstabelecimento().getId().equals(estabelecimentoId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Link não pertence a este estabelecimento");
        }

        linkRepository.delete(link);
    }

    public LinkResponse toResponse(Link link) {
        return LinkResponse.builder()
                .id(link.getId())
                .url(link.getUrl())
                .descricao(link.getDescricao())
                .createdAt(link.getCreatedAt())
                .updatedAt(link.getUpdatedAt())
                .build();
    }
}
