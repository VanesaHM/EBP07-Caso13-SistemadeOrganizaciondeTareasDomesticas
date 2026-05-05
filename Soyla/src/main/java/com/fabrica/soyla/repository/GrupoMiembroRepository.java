package com.fabrica.soyla.repository;

import com.fabrica.soyla.model.GrupoFamiliar;
import com.fabrica.soyla.model.GrupoMiembro;
import com.fabrica.soyla.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GrupoMiembroRepository extends JpaRepository<GrupoMiembro, Long> {
    List<GrupoMiembro> findByGrupoFamiliarAndEstado(GrupoFamiliar grupoFamiliar, GrupoMiembro.EstadoMiembro estado);
    Optional<GrupoMiembro> findByGrupoFamiliarAndUsuario(GrupoFamiliar grupoFamiliar, Usuario usuario);
    boolean existsByGrupoFamiliarAndUsuario(GrupoFamiliar grupoFamiliar, Usuario usuario);
}