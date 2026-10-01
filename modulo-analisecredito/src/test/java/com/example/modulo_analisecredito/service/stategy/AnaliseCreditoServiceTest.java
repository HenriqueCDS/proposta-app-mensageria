package com.example.modulo_analisecredito.service.stategy;

import com.example.modulo_analisecredito.StategyException;
import com.example.modulo_analisecredito.domain.Proposta;
import com.example.modulo_analisecredito.service.stategy.service.NotificacaoRabbitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnaliseCreditoServiceTest {

    private static final String EXCHANGE_CONCLUIDA = "exchange.proposta.concluida";

    @Mock
    private CalculoPonto calculoPonto1;

    @Mock
    private CalculoPonto calculoPonto2;

    @Mock
    private NotificacaoRabbitService notificacaoRabbitService;

    private AnaliseCreditoService analiseCreditoService;

    @BeforeEach
    void setUp() {
        analiseCreditoService = new AnaliseCreditoService(
                List.of(calculoPonto1, calculoPonto2),
                notificacaoRabbitService,
                EXCHANGE_CONCLUIDA);
    }

    @Test
    void deveAprovarPropostaQuandoSomaDePontosForMaiorQuePontuacaoMinima() {
        Proposta proposta = new Proposta();
        when(calculoPonto1.calcular(proposta)).thenReturn(200);
        when(calculoPonto2.calcular(proposta)).thenReturn(200);

        analiseCreditoService.analisar(proposta);

        assertThat(proposta.getAprovado()).isTrue();
        assertThat(proposta.getObservacao()).contains("aprovada").contains("400");
        verify(notificacaoRabbitService).notificar(EXCHANGE_CONCLUIDA, proposta);
    }

    @Test
    void deveReprovarPropostaQuandoSomaDePontosForMenorOuIgualAPontuacaoMinima() {
        Proposta proposta = new Proposta();
        when(calculoPonto1.calcular(proposta)).thenReturn(100);
        when(calculoPonto2.calcular(proposta)).thenReturn(100);

        analiseCreditoService.analisar(proposta);

        assertThat(proposta.getAprovado()).isFalse();
        assertThat(proposta.getObservacao()).contains("reprovada").contains("200");
        verify(notificacaoRabbitService).notificar(EXCHANGE_CONCLUIDA, proposta);
    }

    @Test
    void deveReprovarPropostaQuandoAlgumaStrategyLancarStategyException() {
        Proposta proposta = new Proposta();
        String mensagemErro = "Operação não permitida. Cliente negativado";
        when(calculoPonto1.calcular(proposta)).thenThrow(new StategyException(mensagemErro));

        analiseCreditoService.analisar(proposta);

        assertThat(proposta.getAprovado()).isFalse();
        assertThat(proposta.getObservacao()).isEqualTo(mensagemErro);
        verify(notificacaoRabbitService).notificar(EXCHANGE_CONCLUIDA, proposta);
    }

    @Test
    void deveNotificarConclusaoMesmoQuandoPropostaForReprovada() {
        Proposta proposta = new Proposta();
        when(calculoPonto1.calcular(proposta)).thenReturn(0);
        when(calculoPonto2.calcular(proposta)).thenReturn(0);

        analiseCreditoService.analisar(proposta);

        verify(notificacaoRabbitService).notificar(eq(EXCHANGE_CONCLUIDA), any(Proposta.class));
    }
}
