package com.example.cinema.service;

import com.example.cinema.model.User;
import com.example.cinema.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Serviço responsável por operações relacionadas a usuários, como buscar
 * por nome de usuário e salvar novos registros.
 */
@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Busca um usuário pelo nome de usuário.
     *
     * @param username nome de usuário
     * @return usuário se encontrado
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Salva um usuário no banco de dados.
     *
     * @param user usuário a ser salvo
     * @return usuário salvo
     */
    public User save(User user) {
        return userRepository.save(user);
    }
}