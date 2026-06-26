package com.proposta.app.proposta.app.listener;

import com.proposta.app.proposta.app.entity.Proposta;
import com.proposta.app.proposta.app.repository.PropostaRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PropostaConcluidaListener {

    @Autowired
    private PropostaRepository propostaRepository;

    @RabbitListener(queues = "${rabbit.queue.proposta.concluida}")
    public void propostaConcluida(Proposta proposta){
        PropostaRepository.save(proposta);
    }

}
