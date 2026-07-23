package com.BarbiereDev.cardapio_virtual_BackEnd.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EstabelecimentoRequest {

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    private String slug;

    @Pattern(
            regexp = "^\\+[1-9]\\d{7,14}$",
            message = "Telefone inválido"
    )
    private String telefone;
}
