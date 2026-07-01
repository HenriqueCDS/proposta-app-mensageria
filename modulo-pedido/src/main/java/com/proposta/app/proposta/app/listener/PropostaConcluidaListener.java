package com.proposta.app.proposta.app.listener;

import com.proposta.app.proposta.app.dto.PropostaResponseDto;
import com.proposta.app.proposta.app.entity.Proposta;
import com.proposta.app.proposta.app.mapper.PropostaMapper;
import com.proposta.app.proposta.app.repository.PropostaRepository;
import com.proposta.app.proposta.app.service.PropostaService;
import com.proposta.app.proposta.app.service.WebSocketSevice;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class PropostaConcluidaListener {


    private PropostaRepository propostaRepository;

    private WebSocketSevice webSocketSevice;

    private PropostaService propostaService;

    @RabbitListener(queues = "${rabbit.queue.proposta.concluida}")
    public void propostaConcluida(Proposta proposta){

        atualizarProposta(proposta);
        PropostaResponseDto propostaResponseDto  = PropostaMapper.INSTANCE.convertEntityToDto(proposta);
        webSocketSevice.notificar(propostaResponseDto);

    }

    private void atualizarProposta(Proposta proposta){
        propostaRepository.atualizaProposta(proposta.getId(),proposta.getAprovado(),proposta.getObservacao());
    }

}
