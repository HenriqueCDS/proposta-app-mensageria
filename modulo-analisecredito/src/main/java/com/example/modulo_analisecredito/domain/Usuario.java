package com.example.modulo_analisecredito.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Usuario {


    private Long id;
    private String nome;
    private String sobrenome;
    private String cpf;
    private String telefone;
    private Double renda;
    private Proposta proposta;

}
