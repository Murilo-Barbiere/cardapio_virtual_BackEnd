package com.BarbiereDev.cardapio_virtual_BackEnd.controller;

import com.BarbiereDev.cardapio_virtual_BackEnd.dto.request.EstabelecimentoRequest;
import com.BarbiereDev.cardapio_virtual_BackEnd.dto.response.EstabelecimentoResponse;
import com.BarbiereDev.cardapio_virtual_BackEnd.model.Usuario;
import com.BarbiereDev.cardapio_virtual_BackEnd.service.EstabelecimentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/estabelecimentos")
@RequiredArgsConstructor
public class EstabelecimentoController {

    private final EstabelecimentoService estabelecimentoService;

    @GetMapping
    public ResponseEntity<List<EstabelecimentoResponse>> findAll() {
        return ResponseEntity.ok(estabelecimentoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstabelecimentoResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(estabelecimentoService.findById(id));
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<EstabelecimentoResponse> findBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(estabelecimentoService.findBySlug(slug));
    }

    @PostMapping
    public ResponseEntity<EstabelecimentoResponse> create(
            @Valid @RequestBody EstabelecimentoRequest request,
            @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(estabelecimentoService.create(request, usuarioLogado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EstabelecimentoResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody EstabelecimentoRequest request,
            @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        return ResponseEntity.ok(estabelecimentoService.update(id, request, usuarioLogado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        estabelecimentoService.delete(id, usuarioLogado);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/colaboradores/{usuarioId}")
    public ResponseEntity<EstabelecimentoResponse> adicionarColaborador(
            @PathVariable Long id,
            @PathVariable Long usuarioId,
            @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        return ResponseEntity.ok(estabelecimentoService.adicionarColaborador(id, usuarioId, usuarioLogado));
    }

    @DeleteMapping("/{id}/colaboradores/{usuarioId}")
    public ResponseEntity<EstabelecimentoResponse> removerColaborador(
            @PathVariable Long id,
            @PathVariable Long usuarioId,
            @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        return ResponseEntity.ok(estabelecimentoService.removerColaborador(id, usuarioId, usuarioLogado));
    }
}
