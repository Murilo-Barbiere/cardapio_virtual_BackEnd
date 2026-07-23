package com.BarbiereDev.cardapio_virtual_BackEnd.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemCardapioResponse {

    private Long id;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private Boolean disponivel;
    private Boolean destaque;
    private Integer ordemExibicao;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
