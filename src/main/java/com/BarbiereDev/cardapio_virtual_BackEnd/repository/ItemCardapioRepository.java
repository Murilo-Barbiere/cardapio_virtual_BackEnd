package com.BarbiereDev.cardapio_virtual_BackEnd.repository;

import com.BarbiereDev.cardapio_virtual_BackEnd.model.ItemCardapio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemCardapioRepository extends JpaRepository<ItemCardapio, Long> {

    List<ItemCardapio> findByCategoriaIdOrderByOrdemExibicaoAscNomeAsc(Long categoriaId);

    List<ItemCardapio> findByCategoriaIdAndDisponivelTrueOrderByOrdemExibicaoAscNomeAsc(Long categoriaId);
}
