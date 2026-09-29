package com.example.modulo_analisecredito.service.stategy;

import com.example.modulo_analisecredito.StategyException;
import com.example.modulo_analisecredito.domain.Proposta;
import com.example.modulo_analisecredito.service.stategy.service.NotificacaoRabbitService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnaliseCreditoService {

    private static final int PONTUACAO_MINIMA = 350;

    private final List<CalculoPonto> calculoPontoList;
    private final NotificacaoRabbitService notificacaoRabbitService;
    private final String exchangeProposaConcluida;

    public AnaliseCreditoService(List<CalculoPonto> calculoPontoList,
                                 NotificacaoRabbitService notificacaoRabbitService,
                                 @Value("${rabbitmq.exchange.proposta.concluida}") String exchangeProposaConcluida) {
        this.calculoPontoList = calculoPontoList;
        this.notificacaoRabbitService = notificacaoRabbitService;
        this.exchangeProposaConcluida = exchangeProposaConcluida;
    }


    public void analisar(Proposta proposta){

        try {
            int pontos = calculoPontoList.stream().mapToInt(impl -> impl.calcular(proposta)).sum();

            boolean aprovada = pontos > PONTUACAO_MINIMA;
            proposta.setAprovado(aprovada);

            proposta.setObservacao(aprovada
                    ? "Proposta aprovada com " + pontos + " pontos."
                    : "Proposta reprovada. Pontuação insuficiente: " + pontos + " pontos.");

        }catch (StategyException ex){
            proposta.setAprovado(false);
            proposta.setObservacao(ex.getMessage());
        }

        notificacaoRabbitService.notificar(exchangeProposaConcluida,proposta);

    }


}
