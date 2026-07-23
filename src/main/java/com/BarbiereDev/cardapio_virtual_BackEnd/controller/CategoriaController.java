package com.BarbiereDev.cardapio_virtual_BackEnd.controller;

import com.BarbiereDev.cardapio_virtual_BackEnd.dto.request.CategoriaRequest;
import com.BarbiereDev.cardapio_virtual_BackEnd.dto.response.CategoriaResponse;
import com.BarbiereDev.cardapio_virtual_BackEnd.model.Usuario;
import com.BarbiereDev.cardapio_virtual_BackEnd.service.CategoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;

    @PostMapping("/api/estabelecimentos/{estabelecimentoId}/cardapios/{cardapioId}/categorias")
    public ResponseEntity<CategoriaResponse> criar(
            @PathVariable Long estabelecimentoId,
            @PathVariable Long cardapioId,
            @Valid @RequestBody CategoriaRequest request,
            @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoriaService.create(estabelecimentoId, cardapioId, request, usuarioLogado));
    }

    @GetMapping("/api/estabelecimentos/{estabelecimentoId}/cardapios/{cardapioId}/categorias")
    public ResponseEntity<List<CategoriaResponse>> listar(
            @PathVariable Long estabelecimentoId,
            @PathVariable Long cardapioId
    ) {
        return ResponseEntity.ok(categoriaService.findAllByCardapio(estabelecimentoId, cardapioId));
    }

    @GetMapping("/api/categorias/{id}")
    public ResponseEntity<CategoriaResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.findById(id));
    }

    @PutMapping("/api/categorias/{id}")
    public ResponseEntity<CategoriaResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody CategoriaRequest request,
            @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        return ResponseEntity.ok(categoriaService.update(id, request, usuarioLogado));
    }

    @DeleteMapping("/api/categorias/{id}")
    public ResponseEntity<Void> deletar(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        categoriaService.delete(id, usuarioLogado);
        return ResponseEntity.noContent().build();
    }
}
