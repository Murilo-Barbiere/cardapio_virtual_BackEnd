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
public class EnderecoResponse {

    private Long id;
    private String cidade;
    private String estado;
    private String bairro;
    private String rua;
    private String numero;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
