package com.fabrica.soyla.repository;

import com.fabrica.soyla.model.GrupoFamiliar;
import com.fabrica.soyla.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GrupoFamiliarRepository extends JpaRepository<GrupoFamiliar, Long> {

    @Query("SELECT g FROM GrupoFamiliar g JOIN g.miembros m WHERE m.usuario = :usuario AND m.estado = 'ACTIVO'")
    List<GrupoFamiliar> findGruposByUsuario(@Param("usuario") Usuario usuario);

    boolean existsByNombreAndCreador(String nombre, Usuario creador);
}