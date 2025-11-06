package com.ipn.mx.demowebsocket.basedatos.infrastructure;

import com.ipn.mx.demowebsocket.auth.JwtUtil;
import com.ipn.mx.demowebsocket.basedatos.domain.entity.Administrador;
import com.ipn.mx.demowebsocket.basedatos.domain.repository.AdministradorRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/api/auth/admin")
public class AdminAuthController {
    private final AdministradorRepository repo;
    private final PasswordEncoder encoder;


    public AdminAuthController(AdministradorRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    @Value("${app.jwt.secret}") private String jwtSecret;
    @Value("${app.jwt.expMinutes:120}") private Long expMinutes;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String login = body.getOrDefault("login", "").trim();
        String password = body.getOrDefault("password", "").trim();

        Administrador admin = repo.findByCorreoAdministrador(login)
                .or(()->repo.findByUsuarioAdministrador(login))
                .orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas"));

        if (!encoder.matches(password, admin.getContraseniaAdministrador())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
        }

        String token = JwtUtil.createToken(admin.getIdAdministrador(),
                admin.getUsuarioAdministrador(),
                jwtSecret, expMinutes);

        Map<String, Object> resp = Map.of(
                "accessToken", token,
                "tokenType", "Bearer",
                "expiresIn", expMinutes * 60,
                "admin", Map.of(
                        "idAdministrador", admin.getIdAdministrador(),
                        "nombreAdministrador", admin.getNombreAdministrador(),
                        "usuarioAdministrador", admin.getUsuarioAdministrador(),
                        "correoAdministrador", admin.getCorreoAdministrador()
                )
        );
        return ResponseEntity.ok(resp);

    }
}
