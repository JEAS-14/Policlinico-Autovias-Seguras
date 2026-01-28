package com.policlinico.autovias.infrastructure.persitence.repository;

import com.policlinico.autovias.domain.entity.Consulta;
import com.policlinico.autovias.domain.enums.EstadoConsulta;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConsultaRepository extends JpaRepository<Consulta, Long> {
    
    Optional<Consulta> findByNumeroTicket(String numeroTicket);
    
    Page<Consulta> findByEstado(EstadoConsulta estado, Pageable pageable);
    
    @Query("SELECT c FROM Consulta c WHERE " +
           "LOWER(c.nombre) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
           "LOWER(c.apellido) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
           "LOWER(c.email) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
           "LOWER(c.numeroTicket) LIKE LOWER(CONCAT('%', :termino, '%'))")
    Page<Consulta> buscarPorTermino(@Param("termino") String termino, Pageable pageable);
    
    long countByEstado(EstadoConsulta estado);
    
    @Query("SELECT COUNT(c) FROM Consulta c WHERE c.fechaCreacion >= CURRENT_DATE")
    long countConsultasHoy();
}