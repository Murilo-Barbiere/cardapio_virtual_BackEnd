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
public class LinkResponse {

    private Long id;
    private String url;
    private String descricao;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
