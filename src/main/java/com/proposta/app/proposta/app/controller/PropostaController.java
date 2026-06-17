package com.proposta.app.proposta.app.controller;

import com.proposta.app.proposta.app.dto.PropostaRequestDto;
import com.proposta.app.proposta.app.dto.PropostaResponseDto;
import com.proposta.app.proposta.app.service.PropostaService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/proposta")
public class PropostaController {


    private PropostaService propostaService;

    @PostMapping
    public ResponseEntity<PropostaResponseDto> criar(@RequestBody PropostaRequestDto requestDto){

        PropostaResponseDto response = propostaService.criar(requestDto);

        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(response.getId())
                        .toUri())
                        .body(response);
    }

    @GetMapping
    public ResponseEntity<List<PropostaResponseDto>> obterProposta(){

        List<PropostaResponseDto> response = propostaService.obterProposta();

        return ResponseEntity.ok(response);
    }


}
