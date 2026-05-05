package com.fabrica.soyla.repository;

import com.fabrica.soyla.model.GrupoFamiliar;
import com.fabrica.soyla.model.Tarea;
import com.fabrica.soyla.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TareaRepository extends JpaRepository<Tarea, Long> {

    List<Tarea> findByGrupoFamiliarOrderByFechaCreacionDesc(GrupoFamiliar grupoFamiliar);

    List<Tarea> findByAsignadoA(Usuario usuario);

    @Query("SELECT t FROM Tarea t WHERE t.grupoFamiliar = :grupo AND t.fechaVencimiento = :fecha")
    List<Tarea> findByGrupoFamiliarAndFechaVencimiento(@Param("grupo") GrupoFamiliar grupo,
                                                      @Param("fecha") LocalDate fecha);

    @Query("SELECT t FROM Tarea t WHERE t.grupoFamiliar = :grupo AND t.estado IN :estados")
    List<Tarea> findByGrupoFamiliarAndEstados(@Param("grupo") GrupoFamiliar grupo,
                                             @Param("estados") List<Tarea.EstadoTarea> estados);

    long countByGrupoFamiliarAndEstado(GrupoFamiliar grupoFamiliar, Tarea.EstadoTarea estado);
}