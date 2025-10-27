package com.example.cinema.repository;

import com.example.cinema.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositório responsável por acessar os dados de filmes. Ao estender JpaRepository
 * ganhamos diversos métodos prontos para salvar, buscar, atualizar e deletar
 * entidades. O Spring Boot cria automaticamente a implementação em tempo de
 * execução.
 */
@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    /**
     * Retorna todos os filmes de uma categoria específica.
     *
     * @param category categoria a ser filtrada
     * @return lista de filmes da categoria informada
     */
    List<Movie> findByCategory(String category);
}