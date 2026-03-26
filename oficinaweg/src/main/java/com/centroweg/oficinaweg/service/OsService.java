package com.centroweg.oficinaweg.service;

import com.centroweg.oficinaweg.dto.AbrirOsRequestDTO;
import com.centroweg.oficinaweg.dto.EncerrarOsRequestDTO;
import com.centroweg.oficinaweg.dto.ExecutarOsRequestDTO;
import com.centroweg.oficinaweg.model.*;
import com.centroweg.oficinaweg.repository.OsRepository;
import com.centroweg.oficinaweg.repository.UsuarioRepository;
import com.centroweg.oficinaweg.service.exception.BusinessRuleException;
import com.centroweg.oficinaweg.service.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OsService {

    private final OsRepository osRepository;
    private final UsuarioRepository usuarioRepository;

    public OsService(OsRepository osRepository, UsuarioRepository usuarioRepository) {
        this.osRepository = osRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public String sinalizarProblema(String nome, String equipamento, String defeito) {
        if (isBlank(nome) || isBlank(equipamento) || isBlank(defeito)) {
            throw new BusinessRuleException("Nome, equipamento e defeito são obrigatórios para sinalização.");
        }
        return String.format("[REGISTRO]: %s sinalizou falha no equipamento %s.", nome, equipamento);
    }

    public OrdemServico abrirOS(AbrirOsRequestDTO dto) {
        Usuario usuario = buscarUsuario(dto.getIdProfessor());
        if (!(usuario instanceof Professor professorResponsavel)) {
            throw new SecurityException("Apenas professores podem abrir OS.");
        }

        List<Usuario> usuariosEscalados = usuarioRepository.findAllById(dto.getIdsAlunos());
        if (usuariosEscalados.size() != dto.getIdsAlunos().size()) {
            throw new ResourceNotFoundException("Um ou mais alunos informados não foram encontrados.");
        }

        List<Aluno> alunosEscalados = new ArrayList<>();
        for (Usuario usuarioEscalado : usuariosEscalados) {
            if (!(usuarioEscalado instanceof Aluno aluno)) {
                throw new BusinessRuleException("Somente alunos podem ser escalados para execução da OS.");
            }
            alunosEscalados.add(aluno);
        }

        OrdemServico os = new OrdemServico();
        os.setEquipamento(dto.getEquipamento().trim());
        os.setDefeitoRelatado(dto.getDefeitoRelatado().trim());
        os.setProfessorResponsavel(professorResponsavel);
        os.setStatus(StatusOS.EXECUTANDO);
        os.setAlunosEscalados(alunosEscalados);

        return osRepository.save(os);
    }

    public OrdemServico registrarExecucao(ExecutarOsRequestDTO dto) {
        OrdemServico os = buscarOs(dto.getIdOS());
        Usuario usuario = buscarUsuario(dto.getIdAluno());

        if (!(usuario instanceof Aluno)) {
            throw new SecurityException("Apenas alunos podem registrar execução da OS.");
        }

        boolean estaEscalado = os.getAlunosEscalados().stream()
                .anyMatch(aluno -> aluno.getId().equals(dto.getIdAluno()));

        if (!estaEscalado) {
            throw new SecurityException("Aluno não escalado para esta OS.");
        }

        if (os.getStatus() != StatusOS.EXECUTANDO) {
            throw new BusinessRuleException("A OS precisa estar em execução para receber laudo técnico.");
        }

        os.setMateriaisUsados(dto.getMateriaisUsados().trim());
        os.setLaudoTecnico(dto.getLaudoTecnico().trim());
        os.setStatus(StatusOS.AGUARDANDO_APROVACAO);
        return osRepository.save(os);
    }

    public OrdemServico encerrarOS(EncerrarOsRequestDTO dto) {
        OrdemServico os = buscarOs(dto.getIdOS());
        Usuario usuarioSolicitante = buscarUsuario(dto.getIdProfessor());

        if (!(usuarioSolicitante instanceof Professor professorSolicitante)) {
            throw new SecurityException("Apenas professores podem aprovar e encerrar OS.");
        }

        boolean professorResponsavel = os.getProfessorResponsavel().getId().equals(professorSolicitante.getId());
        boolean coordenador = professorSolicitante instanceof Coordenador;
        if (!professorResponsavel && !coordenador) {
            throw new SecurityException("Somente o professor responsável ou um coordenador pode encerrar a OS.");
        }

        if (os.getStatus() != StatusOS.AGUARDANDO_APROVACAO) {
            throw new BusinessRuleException("A OS só pode ser encerrada após execução e envio para aprovação.");
        }

        validarRastreabilidade(os);
        os.setStatus(StatusOS.CONCLUIDA);
        return osRepository.save(os);
    }

    public List<OrdemServico> listarTodas() {
        return osRepository.findAll();
    }

    private void validarRastreabilidade(OrdemServico os) {
        if (isBlank(os.getEquipamento()) || isBlank(os.getDefeitoRelatado())
                || isBlank(os.getMateriaisUsados()) || isBlank(os.getLaudoTecnico())) {
            throw new BusinessRuleException("A OS deve conter equipamento, defeito, materiais/quantidade e laudo técnico.");
        }
    }

    private OrdemServico buscarOs(Long idOs) {
        return osRepository.findById(idOs)
                .orElseThrow(() -> new ResourceNotFoundException("OS não encontrada."));
    }

    private Usuario buscarUsuario(Long idUsuario) {
        return usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));
    }

    private boolean isBlank(String valor) {
        return valor == null || valor.trim().isEmpty();
    }
}
