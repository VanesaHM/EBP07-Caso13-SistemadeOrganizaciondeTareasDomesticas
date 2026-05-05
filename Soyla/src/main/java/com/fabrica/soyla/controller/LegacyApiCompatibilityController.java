package com.fabrica.soyla.controller;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fabrica.soyla.model.GrupoFamiliar;
import com.fabrica.soyla.model.GrupoMiembro;
import com.fabrica.soyla.model.InvitacionGrupo;
import com.fabrica.soyla.model.RegistroUsuarioDTO;
import com.fabrica.soyla.model.TareaDomestica;
import com.fabrica.soyla.model.Usuario;
import com.fabrica.soyla.repository.GrupoFamiliarRepository;
import com.fabrica.soyla.repository.InvitacionGrupoRepository;
import com.fabrica.soyla.repository.TareaRepository;
import com.fabrica.soyla.repository.UsuarioRepository;
import com.fabrica.soyla.service.InvitacionService;
import com.fabrica.soyla.service.UsuarioService;

@RestController
@RequestMapping("/api")
public class LegacyApiCompatibilityController {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final GrupoFamiliarRepository grupoFamiliarRepository;
    private final TareaRepository tareaRepository;
    private final InvitacionGrupoRepository invitacionGrupoRepository;
    private final InvitacionService invitacionService;

    public LegacyApiCompatibilityController(
        UsuarioService usuarioService,
        UsuarioRepository usuarioRepository,
        GrupoFamiliarRepository grupoFamiliarRepository,
        TareaRepository tareaRepository,
        InvitacionGrupoRepository invitacionGrupoRepository,
        InvitacionService invitacionService
    ) {
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
        this.grupoFamiliarRepository = grupoFamiliarRepository;
        this.tareaRepository = tareaRepository;
        this.invitacionGrupoRepository = invitacionGrupoRepository;
        this.invitacionService = invitacionService;
    }

    @PostMapping("/auth/register")
    public ResponseEntity<?> register(@RequestBody LegacyRegisterRequest request) {
        try {
            RegistroUsuarioDTO dto = new RegistroUsuarioDTO();
            dto.setNombre(requireText(request.fullName(), "El nombre completo es obligatorio."));
            dto.setCorreo(requireText(request.email(), "El correo electronico es obligatorio.").toLowerCase());
            dto.setContrasena(requireText(request.password(), "La contrasena es obligatoria."));

            Usuario user = usuarioService.registrarUsuario(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(new LegacySessionUser(user.getNombre(), user.getCorreo()));
        } catch (IllegalArgumentException | IllegalStateException exception) {
            return error(HttpStatus.BAD_REQUEST, exception.getMessage());
        }
    }

    @GetMapping("/users/{email}")
    public ResponseEntity<?> getUser(@PathVariable String email) {
        return usuarioRepository.findByCorreo(email.toLowerCase())
            .<ResponseEntity<?>>map(user -> ResponseEntity.ok(toLegacyUserProfile(user)))
            .orElseGet(() -> error(HttpStatus.NOT_FOUND, "No se encontro el usuario solicitado."));
    }

    @PutMapping("/users/{email}")
    public ResponseEntity<?> updateUser(@PathVariable String email, @RequestBody LegacyUpdateUserRequest request) {
        Usuario user = usuarioRepository.findByCorreo(email.toLowerCase()).orElse(null);
        if (user == null) {
            return error(HttpStatus.NOT_FOUND, "No se encontro el usuario solicitado.");
        }

        String nextEmail = normalizeOptional(request.email());
        if (nextEmail != null && !nextEmail.equalsIgnoreCase(user.getCorreo())
            && usuarioRepository.existsByCorreo(nextEmail.toLowerCase())) {
            return error(HttpStatus.CONFLICT, "Este correo ya esta registrado por otro usuario.");
        }

        if (nextEmail != null) {
            user.setCorreo(nextEmail.toLowerCase());
        }
        user.setTelefono(normalizeOptional(request.phone()));

        return ResponseEntity.ok(toLegacyUserProfile(usuarioRepository.save(user)));
    }

    @PostMapping("/groups")
    public ResponseEntity<?> createGroup(@RequestBody LegacyCreateGroupRequest request) {
        String name = requireText(request.name(), "El nombre del grupo es obligatorio.");
        String creatorEmail = requireText(request.createdByEmail(), "El creador del grupo es obligatorio.").toLowerCase();

        Usuario creator = usuarioRepository.findByCorreo(creatorEmail).orElse(null);
        if (creator == null) {
            return error(HttpStatus.NOT_FOUND, "No se encontro el usuario solicitado.");
        }

        GrupoFamiliar group = new GrupoFamiliar();
        group.setNombre(name);

        GrupoMiembro membership = new GrupoMiembro();
        membership.setGrupo(group);
        membership.setUsuario(creator);
        membership.setRol("ADMIN");
        group.getMiembros().add(membership);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(toLegacyGroup(grupoFamiliarRepository.save(group)));
    }

    @GetMapping("/groups")
    public ResponseEntity<List<LegacyGroupResponse>> listGroups(@RequestParam String memberEmail) {
        return ResponseEntity.ok(
            grupoFamiliarRepository.findGruposByUsuarioCorreo(memberEmail.toLowerCase()).stream()
                .map(this::toLegacyGroup)
                .toList()
        );
    }

    @GetMapping("/groups/{groupId}")
    public ResponseEntity<?> getGroup(@PathVariable Long groupId) {
        return grupoFamiliarRepository.findById(groupId)
            .<ResponseEntity<?>>map(group -> ResponseEntity.ok(toLegacyGroup(group)))
            .orElseGet(() -> error(HttpStatus.NOT_FOUND, "No se encontro el grupo solicitado."));
    }

    @GetMapping("/groups/{groupId}/members")
    public ResponseEntity<?> listMembers(@PathVariable Long groupId) {
        GrupoFamiliar group = grupoFamiliarRepository.findById(groupId).orElse(null);
        if (group == null) {
            return error(HttpStatus.NOT_FOUND, "No se encontro el grupo solicitado.");
        }

        return ResponseEntity.ok(group.getMiembros().stream()
            .map(this::toLegacyMember)
            .toList());
    }

    @PutMapping("/groups/{groupId}/members/{memberEmail}/role")
    public ResponseEntity<?> updateMemberRole(
        @PathVariable Long groupId,
        @PathVariable String memberEmail,
        @RequestBody LegacyUpdateRoleRequest request
    ) {
        if (!StringUtils.hasText(request.requestedByEmail())) {
            return error(HttpStatus.BAD_REQUEST, "El correo del solicitante es obligatorio.");
        }

        GrupoMiembro requester = grupoFamiliarRepository.findMiembroEnGrupo(groupId, request.requestedByEmail().toLowerCase())
            .orElse(null);
        if (requester == null || !"ADMIN".equalsIgnoreCase(requester.getRol())) {
            return error(HttpStatus.FORBIDDEN, "Solo los administradores pueden cambiar roles.");
        }

        GrupoMiembro target = grupoFamiliarRepository.findMiembroEnGrupo(groupId, memberEmail.toLowerCase()).orElse(null);
        if (target == null) {
            return error(HttpStatus.NOT_FOUND, "No se encontro el miembro solicitado.");
        }

        target.setRol(toInternalRole(request.role()));
        grupoFamiliarRepository.save(target.getGrupo());
        return ResponseEntity.ok(toLegacyMember(target));
    }

    @GetMapping("/groups/{groupId}/invite")
    public ResponseEntity<?> getInvite(@PathVariable Long groupId) {
        try {
            return ResponseEntity.ok(toLegacyInvite(findOrCreateActiveInvite(groupId)));
        } catch (IllegalArgumentException exception) {
            return error(HttpStatus.NOT_FOUND, exception.getMessage());
        }
    }

    @PostMapping("/groups/{groupId}/invite")
    public ResponseEntity<?> createInvite(@PathVariable Long groupId) {
        try {
            return ResponseEntity.ok(toLegacyInvite(findOrCreateActiveInvite(groupId)));
        } catch (IllegalArgumentException exception) {
            return error(HttpStatus.NOT_FOUND, exception.getMessage());
        }
    }

    @GetMapping("/invites/{code}")
    public ResponseEntity<?> lookupInvite(@PathVariable String code) {
        InvitacionGrupo invite = invitacionGrupoRepository.findByToken(code).orElse(null);
        if (invite == null || Boolean.TRUE.equals(invite.getUsado())) {
            return error(HttpStatus.NOT_FOUND, "Enlace de invitacion no valido");
        }
        if (invite.estaExpirada()) {
            return error(HttpStatus.GONE, "Enlace expirado");
        }
        return ResponseEntity.ok(toLegacyInvite(invite));
    }

    @PostMapping("/invites/{code}/join")
    public ResponseEntity<?> joinInvite(@PathVariable String code, @RequestBody LegacyJoinInviteRequest request) {
        InvitacionGrupo invite = invitacionGrupoRepository.findByToken(code).orElse(null);
        if (invite == null) {
            return error(HttpStatus.NOT_FOUND, "Enlace de invitacion no valido");
        }
        if (invite.estaExpirada()) {
            return error(HttpStatus.GONE, "Enlace expirado");
        }

        String userEmail = requireText(request.userEmail(), "El correo del usuario es obligatorio.").toLowerCase();
        Usuario user = usuarioRepository.findByCorreo(userEmail).orElse(null);
        if (user == null) {
            return error(HttpStatus.NOT_FOUND, "No se encontro el usuario solicitado.");
        }

        boolean alreadyMember = groupHasMember(invite.getGrupo(), userEmail);
        if (!alreadyMember) {
            GrupoMiembro member = new GrupoMiembro();
            member.setGrupo(invite.getGrupo());
            member.setUsuario(user);
            member.setRol("MEMBER");
            invite.getGrupo().getMiembros().add(member);
            invite.setUsado(true);
            grupoFamiliarRepository.save(invite.getGrupo());
            invitacionGrupoRepository.save(invite);
        }

        return ResponseEntity.ok(new LegacyJoinInviteResponse(alreadyMember, toLegacyGroup(invite.getGrupo())));
    }

    @GetMapping("/groups/{groupId}/tasks")
    public ResponseEntity<List<LegacyTaskResponse>> listTasks(@PathVariable Long groupId) {
        return ResponseEntity.ok(
            tareaRepository.findByGrupoId(groupId).stream()
                .map(this::toLegacyTask)
                .toList()
        );
    }

    @PostMapping("/groups/{groupId}/tasks")
    public ResponseEntity<?> createTask(@PathVariable Long groupId, @RequestBody LegacyCreateTaskRequest request) {
        GrupoFamiliar group = grupoFamiliarRepository.findById(groupId).orElse(null);
        if (group == null) {
            return error(HttpStatus.NOT_FOUND, "Grupo no encontrado");
        }

        TareaDomestica task = new TareaDomestica();
        task.setGrupo(group);
        task.setNombre(requireText(request.name(), "El nombre de la tarea es obligatorio."));
        task.setDescripcion(StringUtils.hasText(request.description()) ? request.description().trim() : "");
        task.setFechaVencimiento(parseDeadline(request.deadline()));
        task.setFrecuencia(StringUtils.hasText(request.frequency()) ? request.frequency().trim() : "ninguna");
        task.setPrioridad(normalizeOptional(request.priority()));
        task.setEstado("SIN_EMPEZAR");

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(toLegacyTask(tareaRepository.save(task)));
    }

    @PutMapping("/tasks/{taskId}/assignee")
    public ResponseEntity<?> assignTask(@PathVariable Long taskId, @RequestBody LegacyAssignTaskRequest request) {
        TareaDomestica task = tareaRepository.findById(taskId).orElse(null);
        if (task == null) {
            return error(HttpStatus.NOT_FOUND, "Tarea no encontrada");
        }

        String assigneeEmail = requireText(request.assignedToEmail(), "El responsable es obligatorio.").toLowerCase();
        Usuario assignee = usuarioRepository.findByCorreo(assigneeEmail).orElse(null);
        if (assignee == null) {
            return error(HttpStatus.NOT_FOUND, "Usuario no encontrado");
        }
        if (!groupHasMember(task.getGrupo(), assigneeEmail)) {
            return error(HttpStatus.BAD_REQUEST, "El usuario seleccionado no pertenece a este grupo.");
        }

        task.setResponsable(assignee);
        return ResponseEntity.ok(toLegacyTask(tareaRepository.save(task)));
    }

    @DeleteMapping("/tasks/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
        if (!tareaRepository.existsById(taskId)) {
            return ResponseEntity.notFound().build();
        }
        tareaRepository.deleteById(taskId);
        return ResponseEntity.noContent().build();
    }

    private InvitacionGrupo findOrCreateActiveInvite(Long groupId) {
        grupoFamiliarRepository.findById(groupId)
            .orElseThrow(() -> new IllegalArgumentException("Grupo familiar no encontrado"));

        return invitacionGrupoRepository
            .findFirstByGrupo_IdAndUsadoFalseAndFechaExpiracionAfterOrderByFechaCreacionDesc(groupId, LocalDateTime.now())
            .orElseGet(() -> invitacionService.generarInvitacion(groupId));
    }

    private boolean groupHasMember(GrupoFamiliar group, String email) {
        return group.getMiembros().stream()
            .anyMatch(member -> member.getUsuario() != null && email.equalsIgnoreCase(member.getUsuario().getCorreo()));
    }

    private LegacyUserProfileResponse toLegacyUserProfile(Usuario user) {
        return new LegacyUserProfileResponse(user.getId(), user.getNombre(), user.getCorreo(), user.getTelefono(), null);
    }

    private LegacyGroupResponse toLegacyGroup(GrupoFamiliar group) {
        String createdByEmail = group.getMiembros().stream()
            .filter(member -> member.getUsuario() != null)
            .sorted((left, right) -> "ADMIN".equalsIgnoreCase(left.getRol()) ? -1 : 1)
            .map(member -> member.getUsuario().getCorreo())
            .findFirst()
            .orElse(null);

        return new LegacyGroupResponse(group.getId(), group.getNombre(), createdByEmail, null, group.getMiembros().size());
    }

    private LegacyMemberResponse toLegacyMember(GrupoMiembro member) {
        return new LegacyMemberResponse(
            member.getUsuario() == null ? null : member.getUsuario().getCorreo(),
            member.getUsuario() == null ? null : member.getUsuario().getNombre(),
            toLegacyRole(member.getRol()),
            null
        );
    }

    private LegacyInviteResponse toLegacyInvite(InvitacionGrupo invite) {
        return new LegacyInviteResponse(
            invite.getToken(),
            invite.getGrupo().getId(),
            invite.getGrupo().getNombre(),
            toInstantString(invite.getFechaCreacion()),
            toInstantString(invite.getFechaExpiracion())
        );
    }

    private LegacyTaskResponse toLegacyTask(TareaDomestica task) {
        return new LegacyTaskResponse(
            task.getId(),
            task.getGrupo() == null ? null : task.getGrupo().getId(),
            task.getNombre(),
            StringUtils.hasText(task.getDescripcion()) ? task.getDescripcion() : null,
            task.getFechaVencimiento() == null ? null : task.getFechaVencimiento().toString(),
            StringUtils.hasText(task.getFrecuencia()) ? task.getFrecuencia() : "ninguna",
            task.getResponsable() == null ? null : task.getResponsable().getCorreo(),
            task.getResponsable() == null ? null : task.getResponsable().getNombre(),
            normalizeOptional(task.getPrioridad()),
            toLegacyStatus(task.getEstado()),
            null
        );
    }

    private String toLegacyRole(String internalRole) {
        return "ADMIN".equalsIgnoreCase(internalRole) ? "Administrador" : "Colaborador";
    }

    private String toInternalRole(String legacyRole) {
        return "Administrador".equalsIgnoreCase(legacyRole) ? "ADMIN" : "MEMBER";
    }

    private String toLegacyStatus(String internalStatus) {
        if ("COMPLETADA".equalsIgnoreCase(internalStatus) || "COMPLETED".equalsIgnoreCase(internalStatus)) {
            return "completed";
        }
        if ("EN_PROGRESO".equalsIgnoreCase(internalStatus) || "IN_PROGRESS".equalsIgnoreCase(internalStatus)) {
            return "in_progress";
        }
        return "pending";
    }

    private LocalDate parseDeadline(String deadline) {
        if (!StringUtils.hasText(deadline)) {
            return LocalDate.now().plusDays(1);
        }
        return LocalDate.parse(deadline);
    }

    private String toInstantString(LocalDateTime value) {
        return value == null ? null : value.toInstant(ZoneOffset.UTC).toString();
    }

    private String normalizeOptional(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }

    private String requireText(String value, String message) {
        if (!StringUtils.hasText(value)) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }

    private ResponseEntity<Map<String, String>> error(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(Map.of("message", message));
    }

    private record LegacyRegisterRequest(String fullName, String email, String password) {
    }

    private record LegacySessionUser(String fullName, String email) {
    }

    private record LegacyUpdateUserRequest(String email, String phone) {
    }

    private record LegacyUserProfileResponse(Long id, String fullName, String email, String phone, Instant createdAt) {
    }

    private record LegacyCreateGroupRequest(String name, String createdByEmail) {
    }

    private record LegacyGroupResponse(Long id, String name, String createdByEmail, Instant createdAt, long memberCount) {
    }

    private record LegacyMemberResponse(String email, String fullName, String role, Instant joinedAt) {
    }

    private record LegacyUpdateRoleRequest(String role, String requestedByEmail) {
    }

    private record LegacyInviteResponse(String code, Long groupId, String groupName, String createdAt, String expiresAt) {
    }

    private record LegacyJoinInviteRequest(String userEmail) {
    }

    private record LegacyJoinInviteResponse(boolean alreadyMember, LegacyGroupResponse group) {
    }

    private record LegacyCreateTaskRequest(String name, String description, String deadline, String frequency, String priority) {
    }

    private record LegacyTaskResponse(
        Long id,
        Long groupId,
        String name,
        String description,
        String deadline,
        String frequency,
        String assignedToEmail,
        String assignedToName,
        String priority,
        String status,
        Instant createdAt
    ) {
    }

    private record LegacyAssignTaskRequest(String assignedToEmail) {
    }
}
