package com.example.modulo_analisecredito.service.stategy.impl;

import com.example.modulo_analisecredito.StategyException;
import com.example.modulo_analisecredito.constante.MensagemConstante;
import com.example.modulo_analisecredito.domain.Proposta;
import com.example.modulo_analisecredito.service.stategy.CalculoPonto;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Random;

@Order(1)
@Component
public class NomeNegativadoImpl implements CalculoPonto{

    @Override
    public int calcular(Proposta proposta){

        if(nomeNegativado()){
            throw new StategyException(String.format(MensagemConstante.CLiENTE_NEGATIVADO,proposta.getUsuario().getNome()));

        }

        return 100;
    }

    private boolean nomeNegativado(){
        return new Random().nextBoolean();

    }
}
