package com.example.modulo_analisecredito.service.stategy.impl;

import com.example.modulo_analisecredito.domain.Proposta;
import com.example.modulo_analisecredito.domain.Usuario;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RendaMaiorValorSolicitadoTest {

    private final RendaMaiorValorSolicitado strategy = new RendaMaiorValorSolicitado();

    private Proposta propostaCom(double renda, double valorSolicitado) {
        Usuario usuario = new Usuario();
        usuario.setRenda(renda);

        Proposta proposta = new Proposta();
        proposta.setUsuario(usuario);
        proposta.setValorSolicitado(valorSolicitado);
        return proposta;
    }

    @Test
    void devePontuarQuandoRendaForMaiorQueValorSolicitado() {
        Proposta proposta = propostaCom(5000.0, 3000.0);

        assertThat(strategy.calcular(proposta)).isEqualTo(100);
    }

    @Test
    void naoDevePontuarQuandoRendaForIgualAoValorSolicitado() {
        Proposta proposta = propostaCom(3000.0, 3000.0);

        assertThat(strategy.calcular(proposta)).isEqualTo(0);
    }

    @Test
    void naoDevePontuarQuandoRendaForMenorQueValorSolicitado() {
        Proposta proposta = propostaCom(1000.0, 3000.0);

        assertThat(strategy.calcular(proposta)).isEqualTo(0);
    }
}
