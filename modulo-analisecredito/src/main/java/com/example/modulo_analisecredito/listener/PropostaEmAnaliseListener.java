package com.example.modulo_analisecredito.listener;

import com.example.modulo_analisecredito.domain.Proposta;
import com.example.notificacao.constante.MensagemConstante;
import com.example.notificacao.domain.Proposta;
import com.example.notificacao.service.NotificacaoSnsService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PropostaEmAnaliseListener {


    @RabbitListener(queues = "${proposta-pendente.ms-analise-credito}")
    public void propostaPendente(Proposta proposta) {

    }
}
