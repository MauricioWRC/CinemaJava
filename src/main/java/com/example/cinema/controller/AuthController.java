package com.example.cinema.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controller responsável por autenticação simples. Não utiliza banco de dados nem
 * segurança avançada. Apenas valida se usuário e senha são iguais aos
 * valores configurados e responde com sucesso ou erro. Em um sistema
 * real, isso seria implementado com Spring Security e criptografia de senha.
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    // Usuário e senha estáticos para administrador. Contas adicionais são
    // persistidas no banco de dados.
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "1234";

    private final com.example.cinema.service.UserService userService;

    public AuthController(com.example.cinema.service.UserService userService) {
        this.userService = userService;
    }

    /**
     * Endpoint para realizar login. Recebe JSON com campos "username" e
     * "password". Retorna 200 caso as credenciais estejam corretas ou 401 se
     * estiverem incorretas.
     *
     * @param credentials mapa contendo usuário e senha
     * @return resposta indicando sucesso ou falha
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");
        // Verifica credenciais do administrador estático
        if (ADMIN_USERNAME.equals(username) && ADMIN_PASSWORD.equals(password)) {
            return ResponseEntity.ok().build();
        }
        // Procura usuário no banco
        var optionalUser = userService.findByUsername(username);
        if (optionalUser.isPresent() && optionalUser.get().getPassword().equals(password)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    /**
     * Endpoint para registrar um novo usuário. Recebe JSON com campos
     * "username" e "password". Caso o usuário já exista, responde 409 (CONFLICT).
     * Senão, cria e persiste o usuário e retorna 201 (CREATED).
     *
     * @param credentials dados de login
     * @return resposta indicando sucesso ou conflito
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return ResponseEntity.badRequest().body("Username e senha são obrigatórios");
        }
        // Verifica se já existe usuário
        if (userService.findByUsername(username).isPresent() || ADMIN_USERNAME.equals(username)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Usuário já existe");
        }
        // Salva novo usuário
        com.example.cinema.model.User user = new com.example.cinema.model.User();
        user.setUsername(username);
        user.setPassword(password);
        userService.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}