package com.proposta.app.proposta.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PropostaRequestDto {

    private String nome;
    private String sobrenome;
    private String cpf;
    private String telefone;
    private Double renda;
    private Double valorSolicitado;
    private int prazoPagamento;
}
