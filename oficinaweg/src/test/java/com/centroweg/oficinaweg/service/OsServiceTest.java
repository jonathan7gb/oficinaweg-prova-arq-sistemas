package com.centroweg.oficinaweg.service;

import com.centroweg.oficinaweg.dto.AbrirOsRequestDTO;
import com.centroweg.oficinaweg.dto.EncerrarOsRequestDTO;
import com.centroweg.oficinaweg.dto.ExecutarOsRequestDTO;
import com.centroweg.oficinaweg.model.*;
import com.centroweg.oficinaweg.repository.OsRepository;
import com.centroweg.oficinaweg.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OsServiceTest {

    @Mock
    private OsRepository osRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private OsService osService;

    private Professor professor;
    private Aluno aluno;
    private Coordenador coordenador;

    @BeforeEach
    void setUp() {
        professor = new Professor();
        professor.setId(1L);
        professor.setNome("Prof. Ricardo");

        aluno = new Aluno();
        aluno.setId(2L);
        aluno.setNome("Aluno João");

        coordenador = new Coordenador();
        coordenador.setId(3L);
        coordenador.setNome("Coordenador Alexandre");
    }

    @Test
    void deveAbrirOsQuandoSolicitanteEhProfessor() {
        AbrirOsRequestDTO dto = new AbrirOsRequestDTO();
        dto.setIdProfessor(1L);
        dto.setEquipamento("Torno CNC");
        dto.setDefeitoRelatado("Não liga");
        dto.setIdsAlunos(List.of(2L));

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(professor));
        when(usuarioRepository.findAllById(List.of(2L))).thenReturn(List.of(aluno));
        when(osRepository.save(any(OrdemServico.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrdemServico aberta = osService.abrirOS(dto);

        assertEquals(StatusOS.EXECUTANDO, aberta.getStatus());
        assertEquals("Torno CNC", aberta.getEquipamento());
        assertEquals(1, aberta.getAlunosEscalados().size());
    }

    @Test
    void naoDeveAbrirOsQuandoSolicitanteNaoForProfessor() {
        AbrirOsRequestDTO dto = new AbrirOsRequestDTO();
        dto.setIdProfessor(2L);
        dto.setEquipamento("Furadeira");
        dto.setDefeitoRelatado("Sem potência");
        dto.setIdsAlunos(List.of(2L));

        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(aluno));

        assertThrows(SecurityException.class, () -> osService.abrirOS(dto));
        verify(osRepository, never()).save(any());
    }

    @Test
    void deveRegistrarExecucaoQuandoAlunoEscalado() {
        OrdemServico os = new OrdemServico();
        os.setId(10L);
        os.setStatus(StatusOS.EXECUTANDO);
        os.setProfessorResponsavel(professor);
        os.setAlunosEscalados(List.of(aluno));

        ExecutarOsRequestDTO dto = new ExecutarOsRequestDTO();
        dto.setIdOS(10L);
        dto.setIdAluno(2L);
        dto.setMateriaisUsados("2 fusíveis de 10A");
        dto.setLaudoTecnico("Substituição realizada e equipamento testado");

        when(osRepository.findById(10L)).thenReturn(Optional.of(os));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(aluno));
        when(osRepository.save(any(OrdemServico.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrdemServico atualizada = osService.registrarExecucao(dto);

        assertEquals(StatusOS.AGUARDANDO_APROVACAO, atualizada.getStatus());
        assertEquals("2 fusíveis de 10A", atualizada.getMateriaisUsados());
    }

    @Test
    void devePermitirEncerrarQuandoSolicitanteForCoordenador() {
        OrdemServico os = new OrdemServico();
        os.setId(20L);
        os.setStatus(StatusOS.AGUARDANDO_APROVACAO);
        os.setProfessorResponsavel(professor);
        os.setEquipamento("Compressor");
        os.setDefeitoRelatado("Superaquecendo");
        os.setMateriaisUsados("1 rolamento + 1 litro de óleo");
        os.setLaudoTecnico("Troca de rolamento e lubrificação concluídas");

        EncerrarOsRequestDTO dto = new EncerrarOsRequestDTO();
        dto.setIdOS(20L);
        dto.setIdProfessor(3L);

        when(osRepository.findById(20L)).thenReturn(Optional.of(os));
        when(usuarioRepository.findById(3L)).thenReturn(Optional.of(coordenador));
        when(osRepository.save(any(OrdemServico.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrdemServico encerrada = osService.encerrarOS(dto);

        ArgumentCaptor<OrdemServico> captor = ArgumentCaptor.forClass(OrdemServico.class);
        verify(osRepository).save(captor.capture());
        assertEquals(StatusOS.CONCLUIDA, captor.getValue().getStatus());
        assertEquals(StatusOS.CONCLUIDA, encerrada.getStatus());
    }
}

