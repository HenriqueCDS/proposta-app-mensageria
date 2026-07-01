package com.proposta.app.proposta.app.service;

import com.proposta.app.proposta.app.entity.Proposta;
import lombok.AllArgsConstructor;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class NotificacaoService {

    private RabbitTemplate rabbitTemplate;

    public void notificar(Proposta proposta, String exchange, MessagePostProcessor messagePostProcessor){
        rabbitTemplate.convertAndSend(exchange,"",proposta, messagePostProcessor);
    }

    public void notificar(Proposta proposta, String exchange){
        rabbitTemplate.convertAndSend(exchange,"",proposta);
    }

}
