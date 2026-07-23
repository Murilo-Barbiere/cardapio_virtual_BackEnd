package com.BarbiereDev.cardapio_virtual_BackEnd.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemOrdemRequest {

    @NotNull(message = "Ordem de exibição é obrigatória")
    @Min(value = 0, message = "Ordem de exibição deve ser maior ou igual a zero")
    private Integer ordemExibicao;
}
