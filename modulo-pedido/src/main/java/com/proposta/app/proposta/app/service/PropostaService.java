package com.proposta.app.proposta.app.service;

import com.proposta.app.proposta.app.dto.PropostaRequestDto;
import com.proposta.app.proposta.app.dto.PropostaResponseDto;
import com.proposta.app.proposta.app.entity.Proposta;
import com.proposta.app.proposta.app.mapper.PropostaMapper;
import com.proposta.app.proposta.app.repository.PropostaRepository;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PropostaService {

    private String exchangePedente;

    private String exchangeConcluida;

    private PropostaRepository propostaRepository;

    private NotificacaoService notficacaoService;

    public PropostaService(  @Value("${rabbitmq.propostapendente.exchange}") String exchangePedente,
                             @Value("${rabbitmq.propostaconcluida.exchange}") String exchangeConcluida,
                             PropostaRepository propostaRepository,
                             NotificacaoService notficacaoService) {
        this.exchangePedente = exchangePedente;
        this.exchangeConcluida = exchangeConcluida;
        this.propostaRepository = propostaRepository;
        this.notficacaoService = notficacaoService;
    }



    public PropostaResponseDto criar(PropostaRequestDto requestDto ){

        Proposta proposta =  PropostaMapper.INSTANCE.convertDtoToProposta(requestDto);
        propostaRepository.save(proposta);

        //Dar prioridade a um tipo de mensagem específico
        int prioridade = proposta.getUsuario().getRenda() > 10000 ? 10 : 5;

        MessagePostProcessor messagePostProcessor = message -> {
            message.getMessageProperties().setPriority(prioridade);
            return message;
        };


        notificarPendenteRabbitMQ(proposta,messagePostProcessor);

        return PropostaMapper.INSTANCE.convertEntityToDto(proposta);

    }

    public void notificarPendenteRabbitMQ(Proposta proposta, MessagePostProcessor messagePostProcessor){
        try{

            notficacaoService.notificar(proposta,exchangePedente,messagePostProcessor);

        } catch(RuntimeException ex) {

            proposta.setIntegrada(false);

            propostaRepository.save(proposta);
        }
    }
    public void notificarPendenteRabbitMQ(Proposta proposta){
        try{

            notficacaoService.notificar(proposta,exchangePedente);

        } catch(RuntimeException ex) {

            proposta.setIntegrada(false);

            propostaRepository.save(proposta);
        }
    }

    public void notificarConcluidoRabbitMQ(Proposta proposta){
        try{

            notficacaoService.notificar(proposta,exchangeConcluida);

        } catch(RuntimeException ex) {

            System.out.println(ex);

        }
    }

    public List<PropostaResponseDto> obterProposta() {

        return PropostaMapper.INSTANCE.convertListEntityTolistDTO(propostaRepository.findAll());

    }
}
