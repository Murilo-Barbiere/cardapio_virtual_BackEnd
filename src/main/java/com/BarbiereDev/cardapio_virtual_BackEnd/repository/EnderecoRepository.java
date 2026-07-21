package com.BarbiereDev.cardapio_virtual_BackEnd.repository;

import com.BarbiereDev.cardapio_virtual_BackEnd.model.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnderecoRepository extends JpaRepository<Endereco, Long> {

    List<Endereco> findByEstabelecimentoId(Long estabelecimentoId);

    boolean existsByEstabelecimentoIdAndCidadeAndEstadoAndBairroAndRuaAndNumero(
            Long estabelecimentoId, String cidade, String estado, String bairro, String rua, String numero
    );
}
