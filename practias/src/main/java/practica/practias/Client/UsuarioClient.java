package practica.practias.Client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import practica.practias.model.Usuario;

@FeignClient(name = "Gestion-usuario", url = "${app.usuario-service.url}")
public interface UsuarioClient {
 
   // por ahora no implementado

 @GetMapping("/usuarios/{id}")
    ResponseEntity<Usuario> getUsuarioById(@PathVariable("id") Integer id);
    
    @GetMapping("/usuarios/nombre/{nombre}")
    ResponseEntity<Usuario> getUsuarioByNombre(@PathVariable("nombre") String nombre);
    
    @GetMapping("/usuarios/validar/{id}")
    ResponseEntity<Boolean> validarUsuario(@PathVariable("id") Integer id);


}
