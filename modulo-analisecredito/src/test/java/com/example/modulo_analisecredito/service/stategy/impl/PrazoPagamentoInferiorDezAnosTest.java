package com.example.modulo_analisecredito.service.stategy.impl;

import com.example.modulo_analisecredito.domain.Proposta;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PrazoPagamentoInferiorDezAnosTest {

    private final PrazoPagamentoInferiorDezAnos strategy = new PrazoPagamentoInferiorDezAnos();

    @Test
    void devePontuarQuandoPrazoForMenorQueCentoEVinteMeses() {
        Proposta proposta = new Proposta();
        proposta.setPrazoPagamento(119);

        assertThat(strategy.calcular(proposta)).isEqualTo(80);
    }

    @Test
    void naoDevePontuarQuandoPrazoForIgualACentoEVinteMeses() {
        Proposta proposta = new Proposta();
        proposta.setPrazoPagamento(120);

        assertThat(strategy.calcular(proposta)).isEqualTo(0);
    }

    @Test
    void naoDevePontuarQuandoPrazoForMaiorQueCentoEVinteMeses() {
        Proposta proposta = new Proposta();
        proposta.setPrazoPagamento(180);

        assertThat(strategy.calcular(proposta)).isEqualTo(0);
    }
}
