package com.ipn.mx.demowebsocket.basedatos.infrastructure;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.Combate;
import com.ipn.mx.demowebsocket.basedatos.service.CombateService;
// ¡Importamos la sala de espera!
import com.ipn.mx.demowebsocket.servidor.PendingConnectionRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/apiCombates")
public class CombateController {

    // Dependencias finales
    private final CombateService combateService;
    private final PendingConnectionRegistry pendingConnectionRegistry;

    // Inyección por constructor (práctica recomendada)
    @Autowired
    public CombateController(CombateService combateService, PendingConnectionRegistry pendingConnectionRegistry) {
        this.combateService = combateService;
        this.pendingConnectionRegistry = pendingConnectionRegistry;
    }

    @GetMapping("/combate")
    public List<Combate> readAll() { return combateService.readAll(); }

    @GetMapping("/combate/{id}")
    public Combate read(@PathVariable Integer id) { return combateService.read(id); }

    @PostMapping("/combate")
    @ResponseStatus(HttpStatus.CREATED)
    public Combate save(@RequestBody Combate combate) { return combateService.save(combate); }

    @DeleteMapping("/combate/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) { combateService.delete(id); }

    @PutMapping("/combate/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public Combate update(@PathVariable Integer id, @RequestBody Combate combate) {
        Combate c = combateService.read(id);
        return combateService.save(c);
    }

    @PostMapping("/combate/{id}/prepare")
    public ResponseEntity<String> prepareWebSocketConnection(@PathVariable Integer id) {
        Combate combate = combateService.read(id);
        if (combate != null) {
            Long combateId = Long.valueOf(id);
            pendingConnectionRegistry.prepareForRojo(combateId);
            pendingConnectionRegistry.prepareForAzul(combateId);
            return ResponseEntity.ok("Servidor listo para recibir conexiones para el combate " + id);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Combate con id " + id + " no encontrado.");
        }
    }
}