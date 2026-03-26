package com.centroweg.oficinaweg.dto;

import lombok.Data;

import java.util.List;

@Data
public class OsResponseDTO {
    private Long id;
    private String equipamento;
    private String status;
    private String professorResponsavel;
    private List<String> nomesAlunosEscalados;
    private String laudo;
}
