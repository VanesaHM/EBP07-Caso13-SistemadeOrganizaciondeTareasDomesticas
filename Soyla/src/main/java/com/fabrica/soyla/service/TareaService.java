package com.fabrica.soyla.service;

import com.fabrica.soyla.model.GrupoFamiliar;
import com.fabrica.soyla.model.Tarea;
import com.fabrica.soyla.model.Usuario;
import com.fabrica.soyla.repository.TareaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TareaService {

    @Autowired
    private TareaRepository tareaRepository;

    @Autowired
    private GrupoFamiliarService grupoFamiliarService;

    public Tarea crearTarea(String nombre, String descripcion, GrupoFamiliar grupo,
                           Usuario creador, Usuario asignadoA, LocalDate fechaVencimiento,
                           Tarea.PrioridadTarea prioridad) {

        if (!grupoFamiliarService.esMiembroActivo(grupo, creador)) {
            throw new RuntimeException("No tienes permisos para crear tareas en este grupo");
        }

        if (asignadoA != null && !grupoFamiliarService.esMiembroActivo(grupo, asignadoA)) {
            throw new RuntimeException("El usuario asignado no es miembro del grupo");
        }

        Tarea tarea = new Tarea(nombre, descripcion, grupo, creador, fechaVencimiento, prioridad);
        tarea.setAsignadoA(asignadoA);
        return tareaRepository.save(tarea);
    }

    public List<Tarea> obtenerTareasDeGrupo(GrupoFamiliar grupo, Usuario usuario) {
        if (!grupoFamiliarService.esMiembroActivo(grupo, usuario)) {
            throw new RuntimeException("No tienes acceso a las tareas de este grupo");
        }
        return tareaRepository.findByGrupoFamiliarOrderByFechaCreacionDesc(grupo);
    }

    public List<Tarea> obtenerTareasAsignadasAUsuario(Usuario usuario) {
        return tareaRepository.findByAsignadoA(usuario);
    }

    public Optional<Tarea> obtenerTareaPorId(Long id) {
        return tareaRepository.findById(id);
    }

    public Tarea actualizarTarea(Tarea tarea, Usuario usuario) {
        if (!grupoFamiliarService.esMiembroActivo(tarea.getGrupoFamiliar(), usuario)) {
            throw new RuntimeException("No tienes permisos para modificar esta tarea");
        }

        return tareaRepository.save(tarea);
    }

    public void cambiarEstadoTarea(Long tareaId, Tarea.EstadoTarea nuevoEstado, Usuario usuario) {
        Optional<Tarea> tareaOpt = tareaRepository.findById(tareaId);
        if (tareaOpt.isEmpty()) {
            throw new RuntimeException("Tarea no encontrada");
        }

        Tarea tarea = tareaOpt.get();

        if (!grupoFamiliarService.esMiembroActivo(tarea.getGrupoFamiliar(), usuario)) {
            throw new RuntimeException("No tienes permisos para modificar esta tarea");
        }

        tarea.setEstado(nuevoEstado);

        if (nuevoEstado == Tarea.EstadoTarea.COMPLETADA) {
            tarea.setFechaCompletada(LocalDateTime.now());
        } else {
            tarea.setFechaCompletada(null);
        }

        tareaRepository.save(tarea);
    }

    public void asignarTarea(Long tareaId, Usuario asignadoA, Usuario usuarioActual) {
        Optional<Tarea> tareaOpt = tareaRepository.findById(tareaId);
        if (tareaOpt.isEmpty()) {
            throw new RuntimeException("Tarea no encontrada");
        }

        Tarea tarea = tareaOpt.get();

        if (!grupoFamiliarService.esMiembroActivo(tarea.getGrupoFamiliar(), usuarioActual)) {
            throw new RuntimeException("No tienes permisos para asignar esta tarea");
        }

        if (asignadoA != null && !grupoFamiliarService.esMiembroActivo(tarea.getGrupoFamiliar(), asignadoA)) {
            throw new RuntimeException("El usuario asignado no es miembro del grupo");
        }

        tarea.setAsignadoA(asignadoA);
        tareaRepository.save(tarea);
    }

    public void eliminarTarea(Long tareaId, Usuario usuario) {
        Optional<Tarea> tareaOpt = tareaRepository.findById(tareaId);
        if (tareaOpt.isEmpty()) {
            throw new RuntimeException("Tarea no encontrada");
        }

        Tarea tarea = tareaOpt.get();

        if (!grupoFamiliarService.esMiembroActivo(tarea.getGrupoFamiliar(), usuario) &&
            !tarea.getCreador().getId().equals(usuario.getId())) {
            throw new RuntimeException("No tienes permisos para eliminar esta tarea");
        }

        tareaRepository.delete(tarea);
    }

    public List<Tarea> obtenerTareasPorVencer(GrupoFamiliar grupo, LocalDate fecha) {
        return tareaRepository.findByGrupoFamiliarAndFechaVencimiento(grupo, fecha);
    }

    public long contarTareasPorEstado(GrupoFamiliar grupo, Tarea.EstadoTarea estado) {
        return tareaRepository.countByGrupoFamiliarAndEstado(grupo, estado);
    }
}