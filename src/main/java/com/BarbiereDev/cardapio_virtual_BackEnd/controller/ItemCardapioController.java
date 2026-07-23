package com.BarbiereDev.cardapio_virtual_BackEnd.controller;

import com.BarbiereDev.cardapio_virtual_BackEnd.dto.request.ItemCardapioCreateRequest;
import com.BarbiereDev.cardapio_virtual_BackEnd.dto.request.ItemCardapioUpdateRequest;
import com.BarbiereDev.cardapio_virtual_BackEnd.dto.request.ItemDisponibilidadeRequest;
import com.BarbiereDev.cardapio_virtual_BackEnd.dto.request.ItemDestaqueRequest;
import com.BarbiereDev.cardapio_virtual_BackEnd.dto.request.ItemOrdemRequest;
import com.BarbiereDev.cardapio_virtual_BackEnd.dto.response.ItemCardapioResponse;
import com.BarbiereDev.cardapio_virtual_BackEnd.model.Usuario;
import com.BarbiereDev.cardapio_virtual_BackEnd.service.ItemCardapioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ItemCardapioController {

    private final ItemCardapioService itemCardapioService;

    @PostMapping("/api/categorias/{categoriaId}/itens")
    public ResponseEntity<ItemCardapioResponse> criar(
            @PathVariable Long categoriaId,
            @Valid @RequestBody ItemCardapioCreateRequest request,
            @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(itemCardapioService.create(categoriaId, request, usuarioLogado));
    }

    @GetMapping("/api/categorias/{categoriaId}/itens")
    public ResponseEntity<List<ItemCardapioResponse>> listar(
            @PathVariable Long categoriaId,
            @RequestParam(name = "apenasDisponiveis", defaultValue = "true") Boolean apenasDisponiveis
    ) {
        return ResponseEntity.ok(itemCardapioService.findAllByCategoria(categoriaId, apenasDisponiveis));
    }

    @GetMapping("/api/itens/{id}")
    public ResponseEntity<ItemCardapioResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(itemCardapioService.findById(id));
    }

    @PutMapping("/api/itens/{id}")
    public ResponseEntity<ItemCardapioResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ItemCardapioUpdateRequest request,
            @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        return ResponseEntity.ok(itemCardapioService.update(id, request, usuarioLogado));
    }

    @DeleteMapping("/api/itens/{id}")
    public ResponseEntity<Void> deletar(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        itemCardapioService.delete(id, usuarioLogado);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/api/itens/{id}/disponibilidade")
    public ResponseEntity<ItemCardapioResponse> alterarDisponibilidade(
            @PathVariable Long id,
            @Valid @RequestBody ItemDisponibilidadeRequest request,
            @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        return ResponseEntity.ok(itemCardapioService.atualizarDisponibilidade(id, request.getDisponivel(), usuarioLogado));
    }

    @PatchMapping("/api/itens/{id}/destaque")
    public ResponseEntity<ItemCardapioResponse> alterarDestaque(
            @PathVariable Long id,
            @Valid @RequestBody ItemDestaqueRequest request,
            @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        return ResponseEntity.ok(itemCardapioService.atualizarDestaque(id, request.getDestaque(), usuarioLogado));
    }

    @PatchMapping("/api/itens/{id}/ordem")
    public ResponseEntity<ItemCardapioResponse> alterarOrdem(
            @PathVariable Long id,
            @Valid @RequestBody ItemOrdemRequest request,
            @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        return ResponseEntity.ok(itemCardapioService.atualizarOrdem(id, request.getOrdemExibicao(), usuarioLogado));
    }
}
