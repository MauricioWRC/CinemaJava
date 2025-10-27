package com.example.cinema.service;

import com.example.cinema.model.Movie;
import com.example.cinema.repository.MovieRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Camada de serviço responsável por encapsular as regras de negócio para
 * manipulação de filmes. Aqui podemos adicionar lógicas adicionais caso
 * necessário sem poluir a camada de controle (controllers).
 */
@Service
public class MovieService {
    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    /**
     * Recupera todos os filmes cadastrados.
     *
     * @return lista de filmes
     */
    public List<Movie> findAll() {
        return movieRepository.findAll();
    }

    /**
     * Recupera filmes filtrando pela categoria. Caso category seja null ou vazio,
     * retorna todos os filmes.
     *
     * @param category categoria a filtrar
     * @return lista de filmes da categoria ou todos se a categoria for vazia
     */
    public List<Movie> findByCategory(String category) {
        if (category == null || category.isBlank()) {
            return movieRepository.findAll();
        }
        return movieRepository.findByCategory(category);
    }

    /**
     * Retorna um filme pelo id.
     *
     * @param id identificador do filme
     * @return filme se existir, vazio caso contrário
     */
    public Optional<Movie> findById(Long id) {
        return movieRepository.findById(id);
    }

    /**
     * Persiste um filme no banco de dados.
     *
     * @param movie filme a ser salvo
     * @return filme salvo com id gerado
     */
    public Movie save(Movie movie) {
        return movieRepository.save(movie);
    }

    /**
     * Exclui um filme pelo id.
     *
     * @param id identificador do filme a ser deletado
     */
    public void deleteById(Long id) {
        movieRepository.deleteById(id);
    }
}