package com.proposta.app.proposta.app.service;

import com.proposta.app.proposta.app.dto.PropostaRequestDto;
import com.proposta.app.proposta.app.dto.PropostaResponseDto;
import com.proposta.app.proposta.app.entity.Proposta;
import com.proposta.app.proposta.app.repository.PropostaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PropostaServiceTest {

    private static final String EXCHANGE_PENDENTE = "exchange.proposta.pendente";
    private static final String EXCHANGE_CONCLUIDA = "exchange.proposta.concluida";

    @Mock
    private PropostaRepository propostaRepository;

    @Mock
    private NotificacaoService notficacaoService;

    private PropostaService propostaService;

    @BeforeEach
    void setUp() {
        propostaService = new PropostaService(
                EXCHANGE_PENDENTE,
                EXCHANGE_CONCLUIDA,
                propostaRepository,
                notficacaoService);
    }

    private PropostaRequestDto requestDtoComRenda(double renda) {
        return new PropostaRequestDto(
                "Joao",
                "Silva",
                "12345678900",
                "11999999999",
                renda,
                5000.0,
                24);
    }

    @Test
    void criar_devePersistirPropostaEDispararNotificacaoComPrioridadeAlta() {
        ArgumentCaptor<MessagePostProcessor> captor = ArgumentCaptor.forClass(MessagePostProcessor.class);

        PropostaResponseDto response = propostaService.criar(requestDtoComRenda(15000.0));

        verify(propostaRepository).save(any(Proposta.class));
        verify(notficacaoService).notificar(any(Proposta.class), eq(EXCHANGE_PENDENTE), captor.capture());

        Message message = new Message(new byte[0], new MessageProperties());
        captor.getValue().postProcessMessage(message);
        assertThat(message.getMessageProperties().getPriority()).isEqualTo(10);

        assertThat(response.getNome()).isEqualTo("Joao");
    }

    @Test
    void criar_deveDispararNotificacaoComPrioridadeBaixaQuandoRendaNaoForMaiorQueDezMil() {
        ArgumentCaptor<MessagePostProcessor> captor = ArgumentCaptor.forClass(MessagePostProcessor.class);

        propostaService.criar(requestDtoComRenda(10000.0));

        verify(notficacaoService).notificar(any(Proposta.class), eq(EXCHANGE_PENDENTE), captor.capture());

        Message message = new Message(new byte[0], new MessageProperties());
        captor.getValue().postProcessMessage(message);
        assertThat(message.getMessageProperties().getPriority()).isEqualTo(5);
    }

    @Test
    void notificarPendenteRabbitMQ_deveMarcarPropostaComoNaoIntegradaQuandoNotificacaoFalhar() {
        Proposta proposta = new Proposta();
        proposta.setIntegrada(true);
        MessagePostProcessor messagePostProcessor = message -> message;

        doThrow(new RuntimeException("rabbit indisponivel"))
                .when(notficacaoService).notificar(proposta, EXCHANGE_PENDENTE, messagePostProcessor);

        propostaService.notificarPendenteRabbitMQ(proposta, messagePostProcessor);

        assertThat(proposta.getIntegrada()).isFalse();
        verify(propostaRepository).save(proposta);
    }

    @Test
    void notificarConcluidoRabbitMQ_naoDeveLancarExcecaoQuandoNotificacaoFalhar() {
        Proposta proposta = new Proposta();

        doThrow(new RuntimeException("rabbit indisponivel"))
                .when(notficacaoService).notificar(proposta, EXCHANGE_CONCLUIDA);

        propostaService.notificarConcluidoRabbitMQ(proposta);

        verify(propostaRepository, never()).save(any());
    }

    @Test
    void obterProposta_deveRetornarListaDeResponseDtoMapeadaDoRepositorio() {
        Proposta proposta = new Proposta();
        proposta.setValorSolicitado(1000.0);
        when(propostaRepository.findAll()).thenReturn(List.of(proposta));

        List<PropostaResponseDto> response = propostaService.obterProposta();

        assertThat(response).hasSize(1);
    }
}
