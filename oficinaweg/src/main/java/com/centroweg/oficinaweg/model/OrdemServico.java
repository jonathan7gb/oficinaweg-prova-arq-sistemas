package com.centroweg.oficinaweg.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class OrdemServico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String equipamento;
    private String defeitoRelatado;

    @Enumerated(EnumType.STRING)
    private StatusOS status;

    private String materiaisUsados;
    private String laudoTecnico;

    @ManyToOne
    @JoinColumn(name = "professor_id")
    private Professor professorResponsavel;

    @ManyToMany
    @JoinTable(
            name = "os_alunos",
            joinColumns = @JoinColumn(name = "os_id"),
            inverseJoinColumns = @JoinColumn(name = "aluno_id")
    )
    private List<Aluno> alunosEscalados;
}
