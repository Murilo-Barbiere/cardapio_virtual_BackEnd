package com.BarbiereDev.cardapio_virtual_BackEnd.service;

import com.BarbiereDev.cardapio_virtual_BackEnd.dto.request.CategoriaRequest;
import com.BarbiereDev.cardapio_virtual_BackEnd.dto.response.CategoriaResponse;
import com.BarbiereDev.cardapio_virtual_BackEnd.model.Cardapio;
import com.BarbiereDev.cardapio_virtual_BackEnd.model.Categoria;
import com.BarbiereDev.cardapio_virtual_BackEnd.model.Estabelecimento;
import com.BarbiereDev.cardapio_virtual_BackEnd.model.Usuario;
import com.BarbiereDev.cardapio_virtual_BackEnd.repository.CardapioRepository;
import com.BarbiereDev.cardapio_virtual_BackEnd.repository.CategoriaRepository;
import com.BarbiereDev.cardapio_virtual_BackEnd.repository.EstabelecimentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final CardapioRepository cardapioRepository;
    private final EstabelecimentoRepository estabelecimentoRepository;

    @Transactional
    public CategoriaResponse create(Long estabelecimentoId, Long cardapioId, CategoriaRequest request, Usuario usuarioLogado) {
        Estabelecimento estabelecimento = estabelecimentoRepository.findById(estabelecimentoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estabelecimento não encontrado"));

        if (!estabelecimento.getCriador().getId().equals(usuarioLogado.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas o criador pode gerenciar categorias");
        }

        Cardapio cardapio = cardapioRepository.findById(cardapioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cardápio não encontrado"));

        if (!cardapio.getEstabelecimento().getId().equals(estabelecimentoId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cardápio não pertence a este estabelecimento");
        }

        Categoria categoria = Categoria.builder()
                .nome(request.getNome())
                .descricao(request.getDescricao())
                .ordemExibicao(request.getOrdemExibicao())
                .cardapio(cardapio)
                .build();

        categoriaRepository.save(categoria);
        return toResponse(categoria);
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponse> findAllByCardapio(Long estabelecimentoId, Long cardapioId) {
        if (!estabelecimentoRepository.existsById(estabelecimentoId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Estabelecimento não encontrado");
        }

        Cardapio cardapio = cardapioRepository.findById(cardapioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cardápio não encontrado"));

        if (!cardapio.getEstabelecimento().getId().equals(estabelecimentoId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cardápio não pertence a este estabelecimento");
        }

        return categoriaRepository.findByCardapioIdOrderByOrdemExibicaoAscNomeAsc(cardapioId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoriaResponse findById(Long id) {
        return categoriaRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria não encontrada"));
    }

    @Transactional
    public CategoriaResponse update(Long id, CategoriaRequest request, Usuario usuarioLogado) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria não encontrada"));

        verificarPermissao(categoria, usuarioLogado);

        categoria.setNome(request.getNome());
        categoria.setDescricao(request.getDescricao());
        categoria.setOrdemExibicao(request.getOrdemExibicao());

        categoriaRepository.save(categoria);
        return toResponse(categoria);
    }

    @Transactional
    public void delete(Long id, Usuario usuarioLogado) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria não encontrada"));

        verificarPermissao(categoria, usuarioLogado);

        categoriaRepository.delete(categoria);
    }

    private void verificarPermissao(Categoria categoria, Usuario usuarioLogado) {
        Long criadorId = categoria.getCardapio().getEstabelecimento().getCriador().getId();
        if (!criadorId.equals(usuarioLogado.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas o criador pode gerenciar esta categoria");
        }
    }

    public CategoriaResponse toResponse(Categoria categoria) {
        return CategoriaResponse.builder()
                .id(categoria.getId())
                .nome(categoria.getNome())
                .descricao(categoria.getDescricao())
                .ordemExibicao(categoria.getOrdemExibicao())
                .createdAt(categoria.getCreatedAt())
                .updatedAt(categoria.getUpdatedAt())
                .build();
    }
}
