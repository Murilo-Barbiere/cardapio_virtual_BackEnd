package com.BarbiereDev.cardapio_virtual_BackEnd.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EstabelecimentoResponse {

    private Long id;
    private String nome;
    private String slug;
    private String telefone;
    private UsuarioResumo criador;
    private Set<UsuarioResumo> colaboradores;
    private List<EnderecoResponse> enderecos;
    private List<LinkResponse> links;
    private List<CardapioResponse> cardapios;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UsuarioResumo {
        private Long id;
        private String nome;
        private String email;
    }
}
