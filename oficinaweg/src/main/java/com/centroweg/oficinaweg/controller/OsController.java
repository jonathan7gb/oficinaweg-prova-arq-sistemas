package com.centroweg.oficinaweg.controller;

import com.centroweg.oficinaweg.dto.AbrirOsRequestDTO;
import com.centroweg.oficinaweg.dto.EncerrarOsRequestDTO;
import com.centroweg.oficinaweg.dto.ExecutarOsRequestDTO;
import com.centroweg.oficinaweg.model.OrdemServico;
import com.centroweg.oficinaweg.service.OsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/os")
public class OsController {

    @Autowired
    private OsService osService;

    @PostMapping("/sinalizar")
    public ResponseEntity<String> sinalizar(@RequestBody AlertaDTO dto) {
        osService.sinalizarProblema(dto.getNome(), dto.getEquipamento(), dto.getDefeito());
        return ResponseEntity.ok("Problema sinalizado com sucesso.");
    }

    @PostMapping("/abrir")
    public ResponseEntity<?> abrirOS(@RequestBody AbrirOsRequestDTO dto) {
        try {
            OrdemServico novaOS = osService.abrirOS(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(novaOS);
        }catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @PatchMapping("/executar")
    public ResponseEntity<String> registrarExecucao(@RequestBody ExecutarOsRequestDTO dto) {
        try {
            osService.registrarExecucao(dto);
            return ResponseEntity.ok("Execução registrada. Aguardando aprovação.");
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @PatchMapping("/encerrar")
    public ResponseEntity<String> encerrarOS(@RequestBody EncerrarOsRequestDTO dto) {
        try {
            osService.encerrarOS(dto);
            return ResponseEntity.ok("Ordem de Serviço encerrada e aprovada.");
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<OrdemServico>> listarTodas() {
        return ResponseEntity.ok(osService.listarTodas());
    }
}
