package com.centroweg.oficinaweg.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@DiscriminatorValue("ALUNO")
@Data
public class Aluno extends Usuario {
    @ManyToOne
    private Turma turma;
}
