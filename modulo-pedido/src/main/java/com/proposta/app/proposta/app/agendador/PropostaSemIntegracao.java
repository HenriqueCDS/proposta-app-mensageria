package com.proposta.app.proposta.app.agendador;

import com.proposta.app.proposta.app.repository.PropostaRepository;
import com.proposta.app.proposta.app.service.NotficacaoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class PropostaSemIntegracao {

    private PropostaRepository propostaRepository;

    private NotficacaoService notficacaoService;

    private String exchange;

    private final Logger logger = LoggerFactory.getLogger(PropostaSemIntegracao.class);

    public PropostaSemIntegracao(PropostaRepository propostaRepository, NotficacaoService notficacaoService, @Value("${rabbitmq.propostapendente.exchange}") String exchange) {
        this.propostaRepository = propostaRepository;
        this.notficacaoService = notficacaoService;
        this.exchange = exchange;
    }

    @Scheduled(fixedDelay = 10, timeUnit =  TimeUnit.SECONDS)
    public void buscarPropostasSemIntegracao(){
        propostaRepository.findAllByIntegradaIsFalse().forEach(proposta -> {

            try{
                System.out.println("Notificar:"+proposta.getId());
                notficacaoService.notificar(proposta,exchange);
                proposta.setIntegrada(true);
                propostaRepository.save(proposta);

            } catch (RuntimeException ex) {
                logger.error("Erro ao notificar proposta: {}", ex.getMessage(), ex);

            }



        });

    }

}
