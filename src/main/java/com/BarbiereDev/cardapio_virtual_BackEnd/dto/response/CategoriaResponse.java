package com.BarbiereDev.cardapio_virtual_BackEnd.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoriaResponse {

    private Long id;
    private String nome;
    private String descricao;
    private Integer ordemExibicao;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
