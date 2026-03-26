package com.centroweg.oficinaweg.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ExecutarOsRequestDTO {
    @NotNull(message = "ID da OS é obrigatório")
    private Long idOS;

    @NotNull(message = "ID do aluno é obrigatório")
    private Long idAluno;

    @NotBlank(message = "Materiais e quantidades são obrigatórios")
    private String materiaisUsados;

    @NotBlank(message = "Laudo técnico é obrigatório")
    private String laudoTecnico;
}
