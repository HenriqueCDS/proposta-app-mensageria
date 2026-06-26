package com.example.modulo_analisecredito.service.stategy.impl;

import com.example.modulo_analisecredito.domain.Proposta;
import com.example.modulo_analisecredito.service.stategy.CalculoPonto;

import java.util.Random;

public class NomeNegativadoImpl implements CalculoPonto{

    @Override
    public int calcular(Proposta proposta){

        if(nomeNegativado()){
            throw new RuntimeException("Nome negativado");

        }

        return 100;
    }

    private boolean nomeNegativado(){
        return new Random().nextBoolean();

    }
}
