package com.centroweg.oficinaweg.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EncerrarOsRequestDTO {
    @NotNull(message = "ID da OS é obrigatório")
    private Long idOS;

    @NotNull(message = "ID do professor/coordenador é obrigatório")
    private Long idProfessor;
}
