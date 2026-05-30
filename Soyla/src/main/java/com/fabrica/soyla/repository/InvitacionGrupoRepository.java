package com.fabrica.soyla.repository;

import com.fabrica.soyla.model.InvitacionGrupo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface InvitacionGrupoRepository extends JpaRepository<InvitacionGrupo, Long> {

    Optional<InvitacionGrupo> findByToken(String token);

    Optional<InvitacionGrupo> findFirstByGrupo_IdAndUsadoFalseAndFechaExpiracionAfterOrderByFechaCreacionDesc(
        Long grupoId,
        LocalDateTime ahora
    );

    @Modifying
    @Query("DELETE FROM InvitacionGrupo i WHERE i.grupo.id = :grupoId")
    void deleteByGrupoId(@Param("grupoId") Long grupoId);

}