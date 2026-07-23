package com.BarbiereDev.cardapio_virtual_BackEnd.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemDestaqueRequest {

    @NotNull(message = "Destaque é obrigatório")
    private Boolean destaque;
}
