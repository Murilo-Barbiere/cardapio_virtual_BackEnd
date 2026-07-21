package com.BarbiereDev.cardapio_virtual_BackEnd.controller;

import com.BarbiereDev.cardapio_virtual_BackEnd.dto.request.EnderecoRequest;
import com.BarbiereDev.cardapio_virtual_BackEnd.dto.response.EnderecoResponse;
import com.BarbiereDev.cardapio_virtual_BackEnd.model.Usuario;
import com.BarbiereDev.cardapio_virtual_BackEnd.service.EnderecoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/estabelecimentos")
@RequiredArgsConstructor
public class EnderecoController {

    private final EnderecoService enderecoService;

    @PostMapping("/{id}/enderecos")
    public ResponseEntity<EnderecoResponse> criar(
            @PathVariable Long id,
            @Valid @RequestBody EnderecoRequest request,
            @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(enderecoService.create(id, request, usuarioLogado));
    }

    @PutMapping("/{id}/enderecos/{enderecoId}")
    public ResponseEntity<EnderecoResponse> atualizar(
            @PathVariable Long id,
            @PathVariable Long enderecoId,
            @Valid @RequestBody EnderecoRequest request,
            @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        return ResponseEntity.ok(enderecoService.update(id, enderecoId, request, usuarioLogado));
    }

    @DeleteMapping("/{id}/enderecos/{enderecoId}")
    public ResponseEntity<Void> deletar(
            @PathVariable Long id,
            @PathVariable Long enderecoId,
            @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        enderecoService.delete(id, enderecoId, usuarioLogado);
        return ResponseEntity.noContent().build();
    }
}
