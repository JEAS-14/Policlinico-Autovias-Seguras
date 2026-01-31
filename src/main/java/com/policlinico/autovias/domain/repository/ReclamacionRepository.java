package com.policlinico.autovias.domain.repository;

import com.policlinico.autovias.domain.entity.Reclamacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReclamacionRepository extends JpaRepository<Reclamacion, Long> {
    Optional<Reclamacion> findByNumeroTicket(String numeroTicket);
}
