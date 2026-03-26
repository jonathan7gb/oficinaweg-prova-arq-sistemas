package com.centroweg.oficinaweg.controller;

import com.centroweg.oficinaweg.model.Turma;
import com.centroweg.oficinaweg.repository.TurmaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/turmas")
public class TurmaController {

    @Autowired
    private TurmaRepository turmaRepository;

    @GetMapping
    public List<Turma> listarTurmasEAlunos() {
        return turmaRepository.findAll();
    }
}
