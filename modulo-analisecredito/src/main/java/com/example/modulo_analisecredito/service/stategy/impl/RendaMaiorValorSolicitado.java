package com.example.modulo_analisecredito.service.stategy.impl;

import com.example.modulo_analisecredito.domain.Proposta;
import com.example.modulo_analisecredito.service.stategy.CalculoPonto;
import org.springframework.stereotype.Component;

@Component
public class RendaMaiorValorSolicitado implements CalculoPonto {


    @Override
    public int calcular(Proposta proposta) {
        return rendaMaiorValorSociliado(proposta) ? 100 :0;
    }


    private boolean rendaMaiorValorSociliado(Proposta proposta){
        return proposta.getUsuario().getRenda() > proposta.getValorSolicitado();
    }
}
