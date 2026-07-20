package com.BarbiereDev.cardapio_virtual_BackEnd.repository;

import com.BarbiereDev.cardapio_virtual_BackEnd.model.Estabelecimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EstabelecimentoRepository extends JpaRepository<Estabelecimento, Long> {

    Optional<Estabelecimento> findBySlug(String slug);

    boolean existsBySlug(String slug);
}
