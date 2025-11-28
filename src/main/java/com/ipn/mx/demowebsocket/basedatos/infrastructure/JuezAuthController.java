package com.ipn.mx.demowebsocket.basedatos.infrastructure;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping; // <--- ¡AÑADE ESTA LÍNEA!

import java.util.Map;

@RestController
@RequestMapping("/api/auth/juez") // <--- NUEVO ENDPOINT BASE para el login de jueces
@CrossOrigin(origins = {"*"}) // Permite peticiones desde cualquier origen (para desarrollo)
public class JuezAuthController {

    // Contraseña simple para el login de jueces.
    // En un sistema real, esto se validaría contra una base de datos de usuarios/jueces.
    private final String PASSWORD_JUEZ_CORRECTA = "petotech"; // La contraseña que Kivy enviará

    @PostMapping("/login") // Este endpoint completo será /api/auth/juez/login
    public ResponseEntity<?> loginJuez(@RequestBody Map<String, String> credenciales) {
        // La app Kivy enviará un JSON como {"password": "petotech"}
        String password = credenciales.get("password");

        if (password != null && password.equals(PASSWORD_JUEZ_CORRECTA)) {
            System.out.println("✅ Login de Juez HTTP exitoso.");
            return ResponseEntity.ok(Map.of("status", "ok", "message", "Login de Juez exitoso"));
        } else {
            System.out.println("❌ Intento de login de Juez HTTP fallido.");
            return ResponseEntity.status(401).body(Map.of("status", "error", "message", "Contraseña de Juez incorrecta"));
        }
    }
}