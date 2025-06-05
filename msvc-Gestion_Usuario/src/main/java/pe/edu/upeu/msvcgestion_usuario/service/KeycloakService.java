package pe.edu.upeu.msvcgestion_usuario.service;

import pe.edu.upeu.msvcgestion_usuario.entity.dto.UsuarioRegistroDTO;

public interface KeycloakService {
  // Operaciones específicas de Keycloak
    String createUserInKeycloak(UsuarioRegistroDTO dto);
    
    void updateUserInKeycloak(String keycloakId, UsuarioRegistroDTO dto);
    
    void deleteUserFromKeycloak(String keycloakId);
    
    void assignRoleToUser(String keycloakId, String roleName);
    
    void removeRoleFromUser(String keycloakId, String roleName);
    
    boolean existsUserInKeycloak(String username, String email);
    
    void updateUserPassword(String keycloakId, String newPassword);
    
    void enableDisableUser(String keycloakId, boolean enabled);
}
