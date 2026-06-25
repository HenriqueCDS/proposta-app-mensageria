package com.proposta.app.proposta.app.service;

import com.proposta.app.proposta.app.dto.PropostaRequestDto;
import com.proposta.app.proposta.app.dto.PropostaResponseDto;
import com.proposta.app.proposta.app.entity.Proposta;
import com.proposta.app.proposta.app.mapper.PropostaMapper;
import com.proposta.app.proposta.app.repository.PropostaRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PropostaService {

    private String exchange;

    private PropostaRepository propostaRepository;

    private NotficacaoService notficacaoService;

    public PropostaService(  @Value("${rabbitmq.propostapendente.exchange}") String exchange,
                             PropostaRepository propostaRepository,
                             NotficacaoService notficacaoService) {
        this.exchange = exchange;
        this.propostaRepository = propostaRepository;
        this.notficacaoService = notficacaoService;
    }




    public PropostaResponseDto criar(PropostaRequestDto requestDto ){

        Proposta proposta =  PropostaMapper.INSTANCE.convertDtoToProposta(requestDto);
        propostaRepository.save(proposta);

        notificarRabbitMQ(proposta);

        return PropostaMapper.INSTANCE.convertEntityToDto(proposta);

    }
    private void notificarRabbitMQ(Proposta proposta){
        try{
            notficacaoService.notificar(proposta,exchange);
        } catch(RuntimeException ex) {
            proposta.setIntegrada(false);
            propostaRepository.save(proposta);
        }
    }

    public List<PropostaResponseDto> obterProposta() {
        return PropostaMapper.INSTANCE.convertListEntityTolistDTO(propostaRepository.findAll());
    }
}
