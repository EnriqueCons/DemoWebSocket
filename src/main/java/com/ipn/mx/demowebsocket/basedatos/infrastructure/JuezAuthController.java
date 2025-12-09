package com.ipn.mx.demowebsocket.basedatos.infrastructure;

import com.ipn.mx.demowebsocket.basedatos.service.CelularService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@RestController
@RequestMapping("/api/auth/juez")
@CrossOrigin(origins = {"*"})
public class JuezAuthController {

    @Autowired
    private CelularService celularService;

    @PostMapping("/login")
    public ResponseEntity<?> loginJuez(@RequestBody Map<String, String> credenciales) {
        String password = credenciales.get("password");

        if (password == null || password.trim().isEmpty()) {
            System.out.println("Intento de login sin contraseña.");
            return ResponseEntity.status(400)
                    .body(Map.of("status", "error", "message", "Contraseña requerida"));
        }

        Integer combateId = celularService.validarPasswordCombate(password);

        if (combateId != null) {
            System.out.println("Login de Juez HTTP exitoso para combate: " + combateId);
            return ResponseEntity.ok(Map.of(
                    "status", "ok",
                    "message", "Login de Juez exitoso",
                    "combateId", combateId
            ));
        } else {
            System.out.println("Intento de login de Juez HTTP fallido - Contraseña incorrecta.");
            return ResponseEntity.status(401)
                    .body(Map.of("status", "error", "message", "Contraseña incorrecta"));
        }
    }
}