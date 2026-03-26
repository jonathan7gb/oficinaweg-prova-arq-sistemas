package com.centroweg.oficinaweg.repository;

import com.centroweg.oficinaweg.model.OrdemServico;
import com.centroweg.oficinaweg.model.StatusOS;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OsRepository extends JpaRepository<OrdemServico, Long> {
    List<OrdemServico> findByStatus(StatusOS status);
}
