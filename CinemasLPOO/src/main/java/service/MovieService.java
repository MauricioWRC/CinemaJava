package service;

import model.Movie;
import repository.MovieRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MovieService {
    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public List<Movie> findAll() {
        return ((MovieService) movieRepository).findAll();
    }

    public List<Movie> findByCategory(String category) {
        if (category == null || category.isBlank()) {
            return ((MovieService) movieRepository).findAll();
        }
        return movieRepository.findByCategory(category);
    }

    public Optional<Movie> findById(Long id) {
        return ((MovieService) movieRepository).findById(id);
    }

   
    public Movie save(Movie movie) {
        return ((MovieService) movieRepository).save(movie);
    }

    public void deleteById(Long id) {
        ((MovieService) movieRepository).deleteById(id);
    }
}