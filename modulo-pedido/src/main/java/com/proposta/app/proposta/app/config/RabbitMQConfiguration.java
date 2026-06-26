package com.proposta.app.proposta.app.config;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;




@Configuration
public class RabbitMQConfiguration {

    @Value("${rabbitmq.propostapendente.exchange}")
    private String exchangepPropostapendente;

    @Value("${rabbitmq.propostaconcluida.exchange}")
    private String exchangepPropostaConcluida;

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
    public RabbitAdmin criarRabbitAdmin(ConnectionFactory connectionFactory ){
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public ApplicationListener<ApplicationReadyEvent> inicializarAdmin(RabbitAdmin rabbitAdmin){
        return  event -> rabbitAdmin.initialize();
    }



    @Bean
    public FanoutExchange criarFanoutExchangePropostaPendente(){
        return ExchangeBuilder.fanoutExchange(exchangepPropostapendente).build();
    }

    @Bean
    public FanoutExchange criarFanoutExchangePropostaConcluida(){
        return ExchangeBuilder.fanoutExchange(exchangepPropostaConcluida).build();
    }

    @Bean
    public Binding criarBindingPropostaPendenteMSAnaliseCredito(){
        return  BindingBuilder.bind(criarFilaPropostaPedenteMsAnaliseCredito()).
                to(criarFanoutExchangePropostaPendente());
    }

    @Bean
    public Binding criarBindingPropostaPendenteMSNotificacao(){
        return  BindingBuilder.bind(criarFilaFilaPropostaPedenteMsNotificao()).
                to(criarFanoutExchangePropostaPendente());
    }

    @Bean
    public Binding criarBindingPropostaConcluidaMSPropostaApp(){
        return  BindingBuilder.bind(criarFilaPropostaConcluidaProposta()).
                to(criarFanoutExchangePropostaConcluida());
    }
    @Bean
    public Binding criarBindingPropostaConcluidaMSnotificacao(){
        return  BindingBuilder.bind(criarFilaPropostaConcluidaProposta()).
                to(criarFanoutExchangePropostaConcluida());
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter(){
        return new Jackson2JsonMessageConverter();
    }


    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());
        return rabbitTemplate;
    }






}
