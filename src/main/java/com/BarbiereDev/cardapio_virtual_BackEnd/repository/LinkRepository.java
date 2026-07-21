package com.BarbiereDev.cardapio_virtual_BackEnd.repository;

import com.BarbiereDev.cardapio_virtual_BackEnd.model.Link;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LinkRepository extends JpaRepository<Link, Long> {

    List<Link> findByEstabelecimentoId(Long estabelecimentoId);
}
