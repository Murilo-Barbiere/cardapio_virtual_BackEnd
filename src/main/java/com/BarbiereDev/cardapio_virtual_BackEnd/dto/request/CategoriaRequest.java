package com.BarbiereDev.cardapio_virtual_BackEnd.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoriaRequest {

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    private String descricao;

    @Min(value = 0, message = "Ordem de exibição deve ser maior ou igual a zero")
    private Integer ordemExibicao;
}
