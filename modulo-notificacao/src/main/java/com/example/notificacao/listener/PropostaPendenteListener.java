package com.example.notificacao.listener;

import com.example.notificacao.constante.MensagemConstante;
import com.example.notificacao.domain.Proposta;
import com.example.notificacao.service.NotificacaoSnsService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PropostaPendenteListener {

    private final NotificacaoSnsService notificacaoSnsService;

    public PropostaPendenteListener(NotificacaoSnsService notificacaoSnsService) {
        this.notificacaoSnsService = notificacaoSnsService;
    }

    @RabbitListener(queues = "${rabbitmq.queue.proposta.pendente}")
    public void propostaPendente(Proposta proposta) {
        String mensagem = String.format(MensagemConstante.PROPOSTA_EM_ANALISE, proposta.getUsuario().getNome());
        System.out.println(mensagem);
        //notificacaoSnsService.notificar(proposta.getUsuario().getTelefone(),mensagem);
    }
}
