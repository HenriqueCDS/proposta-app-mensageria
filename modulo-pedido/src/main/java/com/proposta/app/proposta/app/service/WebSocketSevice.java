package com.proposta.app.proposta.app.service;

import com.proposta.app.proposta.app.dto.PropostaResponseDto;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketSevice {
    private final SimpMessagingTemplate template;

    public WebSocketSevice(SimpMessagingTemplate template) {
        this.template = template;
    }

    public void notificar(PropostaResponseDto proposta){
        template.convertAndSend("/propostas",proposta);
 
    }


}
