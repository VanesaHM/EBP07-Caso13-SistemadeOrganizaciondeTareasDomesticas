package com.fabrica.soyla.repository;

import com.fabrica.soyla.model.GrupoFamiliar;
import com.fabrica.soyla.model.InvitacionGrupo;
import com.fabrica.soyla.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvitacionGrupoRepository extends JpaRepository<InvitacionGrupo, Long> {
    List<InvitacionGrupo> findByEmailDestinoAndEstado(String emailDestino, InvitacionGrupo.EstadoInvitacion estado);
    List<InvitacionGrupo> findByGrupoFamiliarAndEstado(GrupoFamiliar grupoFamiliar, InvitacionGrupo.EstadoInvitacion estado);
    Optional<InvitacionGrupo> findByEmailDestinoAndGrupoFamiliar(String emailDestino, GrupoFamiliar grupoFamiliar);
    boolean existsByEmailDestinoAndGrupoFamiliarAndEstado(String emailDestino, GrupoFamiliar grupoFamiliar, InvitacionGrupo.EstadoInvitacion estado);
}