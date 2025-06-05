package pe.edu.upeu.msvcgestion_usuario.serviceImpl;

import java.time.LocalDateTime;

//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import pe.edu.upeu.msvcgestion_usuario.entity.Usuario;
import pe.edu.upeu.msvcgestion_usuario.entity.Enum.EstadoUsuario;
import pe.edu.upeu.msvcgestion_usuario.entity.Enum.TipoSuscripcion;
import pe.edu.upeu.msvcgestion_usuario.entity.dto.UsuarioCompletoDTO;
import pe.edu.upeu.msvcgestion_usuario.entity.dto.UsuarioRegistroDTO;
import pe.edu.upeu.msvcgestion_usuario.entity.dto.UsuarioResponseDTO;
import pe.edu.upeu.msvcgestion_usuario.repositories.UsuarioRepository;
import pe.edu.upeu.msvcgestion_usuario.service.KeycloakService;
import pe.edu.upeu.msvcgestion_usuario.service.UsuarioService;



@Slf4j
@Service
@Transactional
public class UsuarioServiceImpl  extends GenericServiceImpl<Usuario, Long> implements UsuarioService{
    
    
 private final UsuarioRepository usuarioRepository;
    private final KeycloakService keycloakService;
    
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, KeycloakService keycloakService) {
        super(usuarioRepository);
        this.usuarioRepository = usuarioRepository;
        this.keycloakService = keycloakService;
    }



@Override
    protected void validateEntity(Usuario entity) {
        if (entity.getKeycloakId() == null || entity.getKeycloakId().trim().isEmpty()) {
            throw new RuntimeException("KeycloakId es obligatorio");
        }
        // nuevo 
        if (entity.getNombre() == null || entity.getNombre().trim().isEmpty()) {
            
            throw new RuntimeException("Nombre es obligatorio");
        }

    }
    
    @Override
    public UsuarioResponseDTO registrarUsuario(UsuarioRegistroDTO dto) {
        log.info("Registrando nuevo usuario: {}", dto.getUsername());
        
        try {
            // nueva validacion : verifica que el nombre no exista en la bd local
            if (usuarioRepository.existsByNombre(dto.getNombre())) {
                throw new RuntimeException("Nombre ya existe en la bd local");
            }


            // 1. Crear en Keycloak primero
            String keycloakId = keycloakService.createUserInKeycloak(dto);
            
            // 2. Crear en BD local
            Usuario usuario = new Usuario();
            usuario.setKeycloakId(keycloakId);
            // nuevo
            usuario.setNombre(dto.getNombre());
            
            usuario.setIdioma("español");
            usuario.setTipoSuscripcion(TipoSuscripcion.GRATUITO);
            usuario.setRecibirNotificaciones(true);
            usuario.setEstado(EstadoUsuario.ACTIVO);
            
                
            Usuario savedUsuario = save(usuario); // ← Usa GenericService
            
            log.info("Usuario registrado exitosamente con ID: {}", savedUsuario.getId());
            return mapToResponseDTO(savedUsuario);
            
        } catch (Exception e) {
            log.error("Error registrando usuario: {}", e.getMessage());
            throw new RuntimeException("Error al registrar usuario: " + e.getMessage());
        }
    }
    
    @Override
    public UsuarioResponseDTO findByKeycloakId(String keycloakId) {
        Usuario usuario = usuarioRepository.findByKeycloakId(keycloakId)
            .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        return mapToResponseDTO(usuario);
    }
    
    @Override
    public UsuarioCompletoDTO obtenerUsuarioCompleto(String keycloakId) {
        // Este método se implementará cuando tengamos JWT tokens
        // Por ahora solo datos de BD
        Usuario usuario = usuarioRepository.findByKeycloakId(keycloakId)
            .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
            
        return UsuarioCompletoDTO.builder()
            .id(usuario.getId())
            .keycloakId(usuario.getKeycloakId())
            .nombre(usuario.getNombre()) // nuevo
            .idioma(usuario.getIdioma())
            .fotoPerfil(usuario.getFotoPerfil())
            .biografia(usuario.getBiografia())
            .tipoSuscripcion(usuario.getTipoSuscripcion())
            .recibirNotificaciones(usuario.getRecibirNotificaciones())
            .fechaCreacion(usuario.getFechaCreacion())
            .estado(usuario.getEstado())
            .build();
    }
    
    @Override
    public void actualizarUltimoLogin(String keycloakId) {
        Usuario usuario = usuarioRepository.findByKeycloakId(keycloakId)
            .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        usuario.setUltimoLogin(LocalDateTime.now());
        save(usuario);
    }
    
    @Override
    public long countUsuariosActivos() {
        return usuarioRepository.countByEstado(EstadoUsuario.ACTIVO);
    }
    
    // ===== MÉTODOS DE MAPEO =====
    
    private UsuarioResponseDTO mapToResponseDTO(Usuario usuario) {
        return UsuarioResponseDTO.builder()
            .id(usuario.getId())
            .keycloakId(usuario.getKeycloakId())
            .nombre(usuario.getNombre())  
            .idioma(usuario.getIdioma())
            .fotoPerfil(usuario.getFotoPerfil())
            .biografia(usuario.getBiografia())
            .tipoSuscripcion(usuario.getTipoSuscripcion())
            .recibirNotificaciones(usuario.getRecibirNotificaciones())
            .fechaCreacion(usuario.getFechaCreacion())
            .estado(usuario.getEstado())
            .build();
    }
    
 

    
}
