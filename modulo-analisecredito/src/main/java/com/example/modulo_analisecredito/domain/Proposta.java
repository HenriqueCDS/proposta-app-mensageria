package com.example.modulo_analisecredito.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Proposta {


    private long id;
    private Double valorSolicitado;
    private int prazoPagamento;
    private Boolean aprovado;
    private Boolean integrada;
    private String observacao;

    private Usuario usuario;


}
