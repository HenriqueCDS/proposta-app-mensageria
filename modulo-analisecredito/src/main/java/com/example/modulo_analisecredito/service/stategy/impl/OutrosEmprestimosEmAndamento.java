package com.example.modulo_analisecredito.service.stategy.impl;

import com.example.modulo_analisecredito.domain.Proposta;
import com.example.modulo_analisecredito.service.stategy.CalculoPonto;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class OutrosEmprestimosEmAndamento implements CalculoPonto {

    @Override
    public int calcular(Proposta proposta){
        return outrosEmprstimosEmAndamento() ? 0 : 80;
    }


    private boolean outrosEmprstimosEmAndamento(){
        return new Random().nextBoolean();
    }
}
