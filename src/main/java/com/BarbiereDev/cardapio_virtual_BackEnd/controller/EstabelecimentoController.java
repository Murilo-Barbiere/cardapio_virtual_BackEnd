package com.BarbiereDev.cardapio_virtual_BackEnd.controller;

import com.BarbiereDev.cardapio_virtual_BackEnd.dto.request.EnderecoRequest;
import com.BarbiereDev.cardapio_virtual_BackEnd.dto.request.EstabelecimentoRequest;
import com.BarbiereDev.cardapio_virtual_BackEnd.dto.request.LinkRequest;
import com.BarbiereDev.cardapio_virtual_BackEnd.dto.response.EnderecoResponse;
import com.BarbiereDev.cardapio_virtual_BackEnd.dto.response.EstabelecimentoResponse;
import com.BarbiereDev.cardapio_virtual_BackEnd.dto.response.LinkResponse;
import com.BarbiereDev.cardapio_virtual_BackEnd.model.Usuario;
import com.BarbiereDev.cardapio_virtual_BackEnd.service.EnderecoService;
import com.BarbiereDev.cardapio_virtual_BackEnd.service.EstabelecimentoService;
import com.BarbiereDev.cardapio_virtual_BackEnd.service.LinkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/estabelecimentos")
@RequiredArgsConstructor
public class EstabelecimentoController {

    private final EstabelecimentoService estabelecimentoService;
    private final EnderecoService enderecoService;
    private final LinkService linkService;

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

    @GetMapping("/{id}/colaboradores")
    public ResponseEntity<Set<EstabelecimentoResponse.UsuarioResumo>> listarColaboradores(@PathVariable Long id) {
        return ResponseEntity.ok(estabelecimentoService.listarColaboradores(id));
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

    @PostMapping("/{id}/enderecos")
    public ResponseEntity<EnderecoResponse> criarEndereco(
            @PathVariable Long id,
            @Valid @RequestBody EnderecoRequest request,
            @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(enderecoService.create(id, request, usuarioLogado));
    }

    @PutMapping("/{id}/enderecos/{enderecoId}")
    public ResponseEntity<EnderecoResponse> atualizarEndereco(
            @PathVariable Long id,
            @PathVariable Long enderecoId,
            @Valid @RequestBody EnderecoRequest request,
            @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        return ResponseEntity.ok(enderecoService.update(id, enderecoId, request, usuarioLogado));
    }

    @DeleteMapping("/{id}/enderecos/{enderecoId}")
    public ResponseEntity<Void> deletarEndereco(
            @PathVariable Long id,
            @PathVariable Long enderecoId,
            @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        enderecoService.delete(id, enderecoId, usuarioLogado);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/links")
    public ResponseEntity<LinkResponse> criarLink(
            @PathVariable Long id,
            @Valid @RequestBody LinkRequest request,
            @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(linkService.create(id, request, usuarioLogado));
    }

    @PutMapping("/{id}/links/{linkId}")
    public ResponseEntity<LinkResponse> atualizarLink(
            @PathVariable Long id,
            @PathVariable Long linkId,
            @Valid @RequestBody LinkRequest request,
            @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        return ResponseEntity.ok(linkService.update(id, linkId, request, usuarioLogado));
    }

    @DeleteMapping("/{id}/links/{linkId}")
    public ResponseEntity<Void> deletarLink(
            @PathVariable Long id,
            @PathVariable Long linkId,
            @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        linkService.delete(id, linkId, usuarioLogado);
        return ResponseEntity.noContent().build();
    }
}
