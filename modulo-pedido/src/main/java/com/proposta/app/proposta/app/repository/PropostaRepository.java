package com.proposta.app.proposta.app.repository;

import com.proposta.app.proposta.app.entity.Proposta;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropostaRepository extends CrudRepository<Proposta,Long> {

    List<Proposta> findAllByIntegradaIsFalse();

    @Transactional
    @Modifying
    @Query(value = "UPDATE proposta set aprovado = :aprovado, observacao = :observacao where id = :id", nativeQuery = true)
    void atualizaProposta(@Param("id") Long id, @Param("aprovado") Boolean aprovado, @Param("observacao") String observacao);
}
