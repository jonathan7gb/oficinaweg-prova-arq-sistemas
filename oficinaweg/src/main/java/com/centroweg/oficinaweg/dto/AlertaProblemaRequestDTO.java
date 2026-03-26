package com.centroweg.oficinaweg.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AlertaProblemaRequestDTO {
    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotBlank(message = "Equipamento é obrigatório")
    private String equipamento;

    @NotBlank(message = "Descrição do defeito é obrigatória")
    private String defeito;
}

