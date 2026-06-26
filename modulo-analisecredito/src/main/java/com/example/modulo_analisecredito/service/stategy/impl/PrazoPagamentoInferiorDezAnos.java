package com.example.modulo_analisecredito.service.stategy.impl;

import com.example.modulo_analisecredito.domain.Proposta;
import com.example.modulo_analisecredito.service.stategy.CalculoPonto;

public class PrazoPagamentoInferiorDezAnos implements CalculoPonto {


    @Override
    public int calcular(Proposta proposta) {
        return proposta.getPrazoPagamento() < 120 ? 80 : 0;
    }
}
