package com.BarbiereDev.cardapio_virtual_BackEnd.controller;

import com.BarbiereDev.cardapio_virtual_BackEnd.dto.request.LinkRequest;
import com.BarbiereDev.cardapio_virtual_BackEnd.dto.response.LinkResponse;
import com.BarbiereDev.cardapio_virtual_BackEnd.model.Usuario;
import com.BarbiereDev.cardapio_virtual_BackEnd.service.LinkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/estabelecimentos")
@RequiredArgsConstructor
public class LinkController {

    private final LinkService linkService;

    @PostMapping("/{id}/links")
    public ResponseEntity<LinkResponse> criar(
            @PathVariable Long id,
            @Valid @RequestBody LinkRequest request,
            @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(linkService.create(id, request, usuarioLogado));
    }

    @PutMapping("/{id}/links/{linkId}")
    public ResponseEntity<LinkResponse> atualizar(
            @PathVariable Long id,
            @PathVariable Long linkId,
            @Valid @RequestBody LinkRequest request,
            @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        return ResponseEntity.ok(linkService.update(id, linkId, request, usuarioLogado));
    }

    @DeleteMapping("/{id}/links/{linkId}")
    public ResponseEntity<Void> deletar(
            @PathVariable Long id,
            @PathVariable Long linkId,
            @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        linkService.delete(id, linkId, usuarioLogado);
        return ResponseEntity.noContent().build();
    }
}
