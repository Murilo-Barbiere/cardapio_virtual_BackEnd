package com.BarbiereDev.cardapio_virtual_BackEnd.repository;

import com.BarbiereDev.cardapio_virtual_BackEnd.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    List<Categoria> findByCardapioIdOrderByOrdemExibicaoAscNomeAsc(Long cardapioId);
}
