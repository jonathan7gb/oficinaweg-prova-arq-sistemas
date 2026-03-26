package com.centroweg.oficinaweg.service;

import com.centroweg.oficinaweg.dto.AbrirOsRequestDTO;
import com.centroweg.oficinaweg.dto.EncerrarOsRequestDTO;
import com.centroweg.oficinaweg.dto.ExecutarOsRequestDTO;
import com.centroweg.oficinaweg.model.*;
import com.centroweg.oficinaweg.repository.OsRepository;
import com.centroweg.oficinaweg.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OsService {

    @Autowired
    private OsRepository osRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public void sinalizarProblema(String nome, String equipamento, String defeito) {
        System.out.println("\n[REGISTRO]: " + nome + " sinalizou falha no equipamento " + equipamento + ".");
    }

    public OrdemServico abrirOS(AbrirOsRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.getIdProfessor())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!(usuario instanceof Professor)) {
            throw new SecurityException("ERRO: Acesso negado. Apenas professores podem abrir OS.");
        }

        OrdemServico os = new OrdemServico();
        os.setEquipamento(dto.getEquipamento());
        os.setDefeitoRelatado(dto.getDefeitoRelatado());
        os.setProfessorResponsavel((Professor) usuario);
        os.setStatus(StatusOS.EXECUTANDO);

        List<Aluno> alunos = usuarioRepository.findAllById(dto.getIdsAlunos())
                .stream().map(u -> (Aluno) u).toList();
        os.setAlunosEscalados(alunos);

        return osRepository.save(os);
    }

    public void registrarExecucao(ExecutarOsRequestDTO dto) {
        OrdemServico os = osRepository.findById(dto.getIdOS())
                .orElseThrow(() -> new RuntimeException("OS não encontrada"));

        boolean estaEscalado = os.getAlunosEscalados().stream()
                .anyMatch(a -> a.getId().equals(dto.getIdAluno()));

        if (!estaEscalado || !os.getStatus().equals(StatusOS.EXECUTANDO)) {
            throw new SecurityException("ERRO: Aluno não escalado ou OS em status inválido.");
        }

        os.setMateriaisUsados(dto.getMateriaisUsados());
        os.setLaudoTecnico(dto.getLaudoTecnico());
        os.setStatus(StatusOS.AGUARDANDO_APROVACAO);
        osRepository.save(os);
    }

    public void encerrarOS(EncerrarOsRequestDTO dto) {
        OrdemServico os = osRepository.findById(dto.getIdOS())
                .orElseThrow(() -> new RuntimeException("OS não encontrada"));

        if (!os.getProfessorResponsavel().getId().equals(dto.getIdProfessor())) {
            throw new SecurityException("ERRO: Aprovação negada. Você não é o responsável por esta OS.");
        }

        os.setStatus(StatusOS.CONCLUIDA);
        osRepository.save(os);
    }
}
