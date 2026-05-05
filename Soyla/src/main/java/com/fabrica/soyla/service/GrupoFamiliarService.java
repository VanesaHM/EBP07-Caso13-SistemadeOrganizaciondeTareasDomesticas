package com.fabrica.soyla.service;

import com.fabrica.soyla.model.GrupoFamiliar;
import com.fabrica.soyla.model.GrupoMiembro;
import com.fabrica.soyla.model.Usuario;
import com.fabrica.soyla.repository.GrupoFamiliarRepository;
import com.fabrica.soyla.repository.GrupoMiembroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class GrupoFamiliarService {

    @Autowired
    private GrupoFamiliarRepository grupoFamiliarRepository;

    @Autowired
    private GrupoMiembroRepository grupoMiembroRepository;

    @Autowired
    private UsuarioService usuarioService;

    public GrupoFamiliar crearGrupo(String nombre, String descripcion, Usuario creador) {
        if (grupoFamiliarRepository.existsByNombreAndCreador(nombre, creador)) {
            throw new RuntimeException("Ya tienes un grupo con ese nombre");
        }

        GrupoFamiliar grupo = new GrupoFamiliar(nombre, descripcion, creador);
        grupo = grupoFamiliarRepository.save(grupo);

        // Agregar al creador como miembro activo
        GrupoMiembro miembro = new GrupoMiembro(grupo, creador, GrupoMiembro.EstadoMiembro.ACTIVO);
        grupoMiembroRepository.save(miembro);

        return grupo;
    }

    public List<GrupoFamiliar> obtenerGruposDeUsuario(Usuario usuario) {
        return grupoFamiliarRepository.findGruposByUsuario(usuario);
    }

    public Optional<GrupoFamiliar> obtenerGrupoPorId(Long id) {
        return grupoFamiliarRepository.findById(id);
    }

    public void agregarMiembro(GrupoFamiliar grupo, Usuario usuario) {
        if (grupoMiembroRepository.existsByGrupoFamiliarAndUsuario(grupo, usuario)) {
            throw new RuntimeException("El usuario ya es miembro del grupo");
        }

        GrupoMiembro miembro = new GrupoMiembro(grupo, usuario, GrupoMiembro.EstadoMiembro.ACTIVO);
        grupoMiembroRepository.save(miembro);
    }

    public void removerMiembro(GrupoFamiliar grupo, Usuario usuario) {
        Optional<GrupoMiembro> miembroOpt = grupoMiembroRepository.findByGrupoFamiliarAndUsuario(grupo, usuario);
        if (miembroOpt.isPresent()) {
            GrupoMiembro miembro = miembroOpt.get();
            miembro.setEstado(GrupoMiembro.EstadoMiembro.INACTIVO);
            grupoMiembroRepository.save(miembro);
        }
    }

    public List<GrupoMiembro> obtenerMiembrosActivos(GrupoFamiliar grupo) {
        return grupoMiembroRepository.findByGrupoFamiliarAndEstado(grupo, GrupoMiembro.EstadoMiembro.ACTIVO);
    }

    public boolean esMiembroActivo(GrupoFamiliar grupo, Usuario usuario) {
        Optional<GrupoMiembro> miembro = grupoMiembroRepository.findByGrupoFamiliarAndUsuario(grupo, usuario);
        return miembro.isPresent() && miembro.get().getEstado() == GrupoMiembro.EstadoMiembro.ACTIVO;
    }

    public boolean esCreador(GrupoFamiliar grupo, Usuario usuario) {
        return grupo.getCreador().getId().equals(usuario.getId());
    }
}