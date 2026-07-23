    package com.BarbiereDev.cardapio_virtual_BackEnd.controller;

    import com.BarbiereDev.cardapio_virtual_BackEnd.dto.request.CardapioRequest;
    import com.BarbiereDev.cardapio_virtual_BackEnd.dto.response.CardapioResponse;
    import com.BarbiereDev.cardapio_virtual_BackEnd.model.Usuario;
    import com.BarbiereDev.cardapio_virtual_BackEnd.service.CardapioService;
    import jakarta.validation.Valid;
    import lombok.RequiredArgsConstructor;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.core.annotation.AuthenticationPrincipal;
    import org.springframework.web.bind.annotation.*;

    @RestController
    @RequestMapping("/api/estabelecimentos")
    @RequiredArgsConstructor
    public class CardapioController {

        private final CardapioService cardapioService;

        @PostMapping("/{id}/cardapios")
        public ResponseEntity<CardapioResponse> criar(
                @PathVariable Long id,
                @Valid @RequestBody CardapioRequest request,
                @AuthenticationPrincipal Usuario usuarioLogado
        ) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(cardapioService.create(id, request, usuarioLogado));
        }

        @PutMapping("/{id}/cardapios/{cardapioId}")
        public ResponseEntity<CardapioResponse> atualizar(
                @PathVariable Long id,
                @PathVariable Long cardapioId,
                @Valid @RequestBody CardapioRequest request,
                @AuthenticationPrincipal Usuario usuarioLogado
        ) {
            return ResponseEntity.ok(cardapioService.update(id, cardapioId, request, usuarioLogado));
        }

        @DeleteMapping("/{id}/cardapios/{cardapioId}")
        public ResponseEntity<Void> deletar(
                @PathVariable Long id,
                @PathVariable Long cardapioId,
                @AuthenticationPrincipal Usuario usuarioLogado
        ) {
            cardapioService.delete(id, cardapioId, usuarioLogado);
            return ResponseEntity.noContent().build();
        }
    }
