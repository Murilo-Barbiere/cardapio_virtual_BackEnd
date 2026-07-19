package com.BarbiereDev.cardapio_virtual_BackEnd.dto.response;

import com.BarbiereDev.cardapio_virtual_BackEnd.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {

    private String token;
    private Long id;
    private String nome;
    private String email;
    private Role role;
}
