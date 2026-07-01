package com.example.modulo_analisecredito.service.stategy;

import com.example.modulo_analisecredito.StategyException;
import com.example.modulo_analisecredito.domain.Proposta;
import com.example.modulo_analisecredito.service.stategy.service.NotificacaoRabbitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnaliseCreditoService {

    @Autowired
    private List<CalculoPonto> calculoPontoList;

    @Autowired
    private NotificacaoRabbitService notificacaoRabbitService;

    @Value("${rabbitmq.exchange.proposta.concluida}")
    private String exchangeProposaConcluida;


    public void analisar(Proposta proposta){

        try {
            int pontos = calculoPontoList.stream().mapToInt(impl -> impl.calcular(proposta)).sum();

            proposta.setAprovado( pontos > 350);

            proposta.setObservacao(pontos > 350
                    ? "Proposta aprovada com " + pontos + " pontos."
                    : "Proposta reprovada. Pontuação insuficiente: " + pontos + " pontos.");

        }catch (StategyException ex){
            proposta.setAprovado(false);
            proposta.setObservacao(ex.getMessage());
        }

        notificacaoRabbitService.notificar(exchangeProposaConcluida,proposta);

    }


}
