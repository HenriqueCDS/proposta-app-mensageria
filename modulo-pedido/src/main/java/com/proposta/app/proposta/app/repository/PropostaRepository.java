package com.proposta.app.proposta.app.repository;

import com.proposta.app.proposta.app.dto.PropostaResponseDto;
import com.proposta.app.proposta.app.entity.Proposta;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.core.CrudMethods;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropostaRepository extends CrudRepository<Proposta,Long> {

    List<Proposta> findAllByIntegradaIsFalse();

    @Transactional
    @Modifying
    @Query(value = "UPDATE proposta set aprovada = :aprovada,observacao = :observacao where id = :id", nativeQuery = true)
    void atualizaProposta(Long id,boolean aprovada,String observacao);
}
