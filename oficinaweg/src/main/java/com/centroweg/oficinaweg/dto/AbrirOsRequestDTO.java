package com.centroweg.oficinaweg.dto;

import lombok.Data;

import java.util.List;

@Data
public class AbrirOsRequestDTO {
    private Long idProfessor;
    private String equipamento;
    private String defeitoRelatado;
    private List<Long> idsAlunos;
}
