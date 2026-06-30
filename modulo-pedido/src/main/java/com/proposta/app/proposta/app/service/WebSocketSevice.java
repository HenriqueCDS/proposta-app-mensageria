package com.proposta.app.proposta.app.service;

import com.proposta.app.proposta.app.dto.PropostaResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketSevice {
    @Autowired
    private SimpMessagingTemplate template;

    public void notificar(PropostaResponseDto proposta){
        template.convertAndSend("/propostas",proposta);
 
    }


}
