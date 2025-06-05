package practica.practias.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import practica.practias.model.Exercises;
import practica.practias.service.ExercisesService;
@RestController
@RequestMapping("/api/v1/ejercicios")
public class ExerciseController {

    private final ExercisesService exercisesService;
    
    
    public ExerciseController(ExercisesService exercisesService) {
        this.exercisesService = exercisesService;
    }
    
    @GetMapping("/ALL")
    public ResponseEntity<List<Map<String, Object>>> getAllEjercicios() {
        List<Map<String, Object>> ejercicios = exercisesService.getAllExercises()
            .stream()
            .map(e -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", e.getId());
                map.put("nombre", e.getNombre());
                map.put("descripcion", e.getDescripcion());
                return map;
            })
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(ejercicios);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getEjercicioById(@PathVariable Long id) {
        Optional<Exercises> ejercicio = exercisesService.getExerciseById(id);
        
        if (ejercicio.isPresent()) {
            Map<String, Object> response = new HashMap<>();
            response.put("id", ejercicio.get().getId());
            response.put("nombre", ejercicio.get().getNombre());
            response.put("descripcion", ejercicio.get().getDescripcion());
            response.put("plantilla", ejercicio.get().getPlantilla());
            
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
