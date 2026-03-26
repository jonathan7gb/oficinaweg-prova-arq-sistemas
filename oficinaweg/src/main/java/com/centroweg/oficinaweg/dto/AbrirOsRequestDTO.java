package com.centroweg.oficinaweg.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class AbrirOsRequestDTO {
    @NotNull(message = "ID do professor é obrigatório")
    private Long idProfessor;

    @NotBlank(message = "Equipamento é obrigatório")
    private String equipamento;

    @NotBlank(message = "Defeito relatado é obrigatório")
    private String defeitoRelatado;

    @NotEmpty(message = "É necessário escalar ao menos um aluno")
    @Size(max = 3, message = "Uma OS aceita no máximo 3 alunos")
    private List<Long> idsAlunos;
}
