package com.BarbiereDev.cardapio_virtual_BackEnd.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LinkRequest {

    @NotBlank(message = "URL é obrigatória")
    private String url;

    private String descricao;
}
