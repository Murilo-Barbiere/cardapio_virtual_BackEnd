package com.BarbiereDev.cardapio_virtual_BackEnd.service;

import com.BarbiereDev.cardapio_virtual_BackEnd.dto.request.ItemCardapioCreateRequest;
import com.BarbiereDev.cardapio_virtual_BackEnd.dto.request.ItemCardapioUpdateRequest;
import com.BarbiereDev.cardapio_virtual_BackEnd.dto.response.ItemCardapioResponse;
import com.BarbiereDev.cardapio_virtual_BackEnd.model.Categoria;
import com.BarbiereDev.cardapio_virtual_BackEnd.model.ItemCardapio;
import com.BarbiereDev.cardapio_virtual_BackEnd.model.Usuario;
import com.BarbiereDev.cardapio_virtual_BackEnd.repository.CategoriaRepository;
import com.BarbiereDev.cardapio_virtual_BackEnd.repository.ItemCardapioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemCardapioService {

    private final ItemCardapioRepository itemCardapioRepository;
    private final CategoriaRepository categoriaRepository;

    @Transactional
    public ItemCardapioResponse create(Long categoriaId, ItemCardapioCreateRequest request, Usuario usuarioLogado) {
        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria não encontrada"));

        verificarPermissao(categoria, usuarioLogado);

        ItemCardapio item = ItemCardapio.builder()
                .nome(request.getNome())
                .descricao(request.getDescricao())
                .preco(request.getPreco())
                .disponivel(request.getDisponivel() != null ? request.getDisponivel() : true)
                .destaque(request.getDestaque() != null ? request.getDestaque() : false)
                .ordemExibicao(request.getOrdemExibicao())
                .categoria(categoria)
                .build();

        itemCardapioRepository.save(item);
        return toResponse(item);
    }

    @Transactional(readOnly = true)
    public ItemCardapioResponse findById(Long id) {
        return itemCardapioRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item não encontrado"));
    }

    @Transactional(readOnly = true)
    public List<ItemCardapioResponse> findAllByCategoria(Long categoriaId, Boolean apenasDisponiveis) {
        if (!categoriaRepository.existsById(categoriaId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria não encontrada");
        }

        List<ItemCardapio> itens;
        if (Boolean.TRUE.equals(apenasDisponiveis)) {
            itens = itemCardapioRepository.findByCategoriaIdAndDisponivelTrueOrderByOrdemExibicaoAscNomeAsc(categoriaId);
        } else {
            itens = itemCardapioRepository.findByCategoriaIdOrderByOrdemExibicaoAscNomeAsc(categoriaId);
        }

        return itens.stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ItemCardapioResponse update(Long id, ItemCardapioUpdateRequest request, Usuario usuarioLogado) {
        ItemCardapio item = itemCardapioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item não encontrado"));

        verificarPermissao(item.getCategoria(), usuarioLogado);

        item.setNome(request.getNome());
        item.setDescricao(request.getDescricao());
        item.setPreco(request.getPreco());
        item.setDisponivel(request.getDisponivel() != null ? request.getDisponivel() : item.getDisponivel());
        item.setDestaque(request.getDestaque() != null ? request.getDestaque() : item.getDestaque());
        item.setOrdemExibicao(request.getOrdemExibicao());

        itemCardapioRepository.save(item);
        return toResponse(item);
    }

    @Transactional
    public void delete(Long id, Usuario usuarioLogado) {
        ItemCardapio item = itemCardapioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item não encontrado"));

        verificarPermissao(item.getCategoria(), usuarioLogado);

        itemCardapioRepository.delete(item);
    }

    @Transactional
    public ItemCardapioResponse atualizarDisponibilidade(Long id, Boolean disponivel, Usuario usuarioLogado) {
        ItemCardapio item = itemCardapioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item não encontrado"));

        verificarPermissao(item.getCategoria(), usuarioLogado);

        item.setDisponivel(disponivel);
        itemCardapioRepository.save(item);
        return toResponse(item);
    }

    @Transactional
    public ItemCardapioResponse atualizarDestaque(Long id, Boolean destaque, Usuario usuarioLogado) {
        ItemCardapio item = itemCardapioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item não encontrado"));

        verificarPermissao(item.getCategoria(), usuarioLogado);

        item.setDestaque(destaque);
        itemCardapioRepository.save(item);
        return toResponse(item);
    }

    @Transactional
    public ItemCardapioResponse atualizarOrdem(Long id, Integer ordemExibicao, Usuario usuarioLogado) {
        ItemCardapio item = itemCardapioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item não encontrado"));

        verificarPermissao(item.getCategoria(), usuarioLogado);

        item.setOrdemExibicao(ordemExibicao);
        itemCardapioRepository.save(item);
        return toResponse(item);
    }

    private void verificarPermissao(Categoria categoria, Usuario usuarioLogado) {
        Long criadorId = categoria.getCardapio().getEstabelecimento().getCriador().getId();
        if (!criadorId.equals(usuarioLogado.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas o criador pode gerenciar itens");
        }
    }

    public ItemCardapioResponse toResponse(ItemCardapio item) {
        return ItemCardapioResponse.builder()
                .id(item.getId())
                .nome(item.getNome())
                .descricao(item.getDescricao())
                .preco(item.getPreco())
                .disponivel(item.getDisponivel())
                .destaque(item.getDestaque())
                .ordemExibicao(item.getOrdemExibicao())
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .build();
    }
}
