package com.proposta.app.proposta.app.config;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfiguration {
    @Bean
    public Queue criarFilaPropostaPedenteMsAnaliseCredito(){
        return QueueBuilder.durable("proposta-pendente.ms-analise-credito").build();

    }

    @Bean
    public Queue criarFilaFilaPropostaPedenteMsNotificao(){
        return QueueBuilder.durable("proposta-pendente.ms-notificacao").build();

    }

    @Bean
    public Queue criarFilaPropostaConcluidaProposta(){
        return QueueBuilder.durable("proposta-concluida.ms-analise-credito").build();

    }

    @Bean
    public Queue criarFilaFilaPropostaConcluidaMsNotificao(){
        return QueueBuilder.durable("proposta-concluida.ms-notificacao").build();

    }
    @Bean
    private RabbitAdmin criarRabbitAdmin(ConnectionFactory connectionFactory ){
        return new RabbitAdmin(connectionFactory);
    }




}
