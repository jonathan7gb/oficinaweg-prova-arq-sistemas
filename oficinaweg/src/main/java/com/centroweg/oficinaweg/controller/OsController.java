package com.centroweg.oficinaweg.controller;

import com.centroweg.oficinaweg.dto.AbrirOsRequestDTO;
import com.centroweg.oficinaweg.dto.AlertaProblemaRequestDTO;
import com.centroweg.oficinaweg.dto.EncerrarOsRequestDTO;
import com.centroweg.oficinaweg.dto.ExecutarOsRequestDTO;
import com.centroweg.oficinaweg.model.OrdemServico;
import com.centroweg.oficinaweg.service.OsService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/os")
public class OsController {

    private final OsService osService;

    public OsController(OsService osService) {
        this.osService = osService;
    }

    @PostMapping("/sinalizar")
    public ResponseEntity<Map<String, String>> sinalizar(@Valid @RequestBody AlertaProblemaRequestDTO dto) {
        String registro = osService.sinalizarProblema(dto.getNome(), dto.getEquipamento(), dto.getDefeito());
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", registro));
    }

    @PostMapping("/abrir")
    public ResponseEntity<OrdemServico> abrirOS(@Valid @RequestBody AbrirOsRequestDTO dto) {
        OrdemServico novaOS = osService.abrirOS(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaOS);
    }

    @PatchMapping("/executar")
    public ResponseEntity<OrdemServico> registrarExecucao(@Valid @RequestBody ExecutarOsRequestDTO dto) {
        OrdemServico osAtualizada = osService.registrarExecucao(dto);
        return ResponseEntity.ok(osAtualizada);
    }

    @PatchMapping("/encerrar")
    public ResponseEntity<OrdemServico> encerrarOS(@Valid @RequestBody EncerrarOsRequestDTO dto) {
        OrdemServico osAtualizada = osService.encerrarOS(dto);
        return ResponseEntity.ok(osAtualizada);
    }

    @GetMapping
    public ResponseEntity<List<OrdemServico>> listarTodas() {
        return ResponseEntity.ok(osService.listarTodas());
    }
}
