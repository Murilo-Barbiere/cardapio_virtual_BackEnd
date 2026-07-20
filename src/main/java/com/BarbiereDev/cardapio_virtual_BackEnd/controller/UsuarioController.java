package com.BarbiereDev.cardapio_virtual_BackEnd.controller;

import com.BarbiereDev.cardapio_virtual_BackEnd.dto.request.UsuarioUpdateRequest;
import com.BarbiereDev.cardapio_virtual_BackEnd.dto.response.UsuarioResponse;
import com.BarbiereDev.cardapio_virtual_BackEnd.model.Usuario;
import com.BarbiereDev.cardapio_virtual_BackEnd.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UsuarioResponse>> findAll() {
        return ResponseEntity.ok(usuarioService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> findById(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        return ResponseEntity.ok(usuarioService.findById(id, usuarioLogado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioUpdateRequest request,
            @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        return ResponseEntity.ok(usuarioService.update(id, request, usuarioLogado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        usuarioService.delete(id, usuarioLogado);
        return ResponseEntity.noContent().build();
    }
}
