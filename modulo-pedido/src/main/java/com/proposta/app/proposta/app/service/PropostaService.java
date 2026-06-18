package com.proposta.app.proposta.app.service;

import com.proposta.app.proposta.app.dto.PropostaRequestDto;
import com.proposta.app.proposta.app.dto.PropostaResponseDto;
import com.proposta.app.proposta.app.entity.Proposta;
import com.proposta.app.proposta.app.mapper.PropostaMapper;
import com.proposta.app.proposta.app.repository.PropostaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class PropostaService {

    private PropostaRepository propostaRepository;

    public PropostaResponseDto criar(PropostaRequestDto requestDto ){

        propostaRepository.save(new Proposta());
        Proposta proposta =  PropostaMapper.INSTANCE.convertDtoToProposta(requestDto);
        propostaRepository.save(proposta);

        return PropostaMapper.INSTANCE.convertEntityToDto(proposta);

    }

    public List<PropostaResponseDto> obterProposta() {
        return PropostaMapper.INSTANCE.convertListEntityTolistDTO(propostaRepository.findAll());

    }
}
