package controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import model.Movie;
import service.MovieService;

@RestController
@RequestMapping("/api/movies")
@CrossOrigin(origins = "*")
public class MovieController {
    private final MovieService movieService;

    private final String uploadDir;

    public MovieController(MovieService movieService, @Value("${upload.dir}") String uploadDir) {
        this.movieService = movieService;
        this.uploadDir = uploadDir;
    }

    @GetMapping
    public ResponseEntity<List<Movie>> listMovies(@RequestParam(required = false) String category) {
        List<Movie> movies = movieService.findByCategory(category);
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Movie> getMovie(@PathVariable Long id) {
        Optional<Movie> optionalMovie = movieService.findById(id);
        return optionalMovie.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Movie> createMovie(@RequestBody Movie movie) {
        Movie saved = movieService.save(movie);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PostMapping(consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Movie> createMovieWithFile(
            @RequestParam("title") String title,
            @RequestParam("category") String category,
            @RequestParam("poster") MultipartFile poster) {
        try {
            // Garante que o diretório de uploads existe
            java.nio.file.Path uploadPath = java.nio.file.Paths.get(uploadDir);
            java.nio.file.Files.createDirectories(uploadPath);
            // Gera um nome único para a imagem
            String originalFilename = poster.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String fileName = java.util.UUID.randomUUID() + extension;
            java.nio.file.Path filePath = uploadPath.resolve(fileName);
            // Salva o arquivo no disco
            poster.transferTo(filePath.toFile());
            // Cria e salva o filme
            Movie movie = new Movie();
            movie.setTitle(title);
            movie.setCategory(category);
            movie.setPosterUrl("/uploads/" + fileName);
            Movie saved = movieService.save(movie);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Movie> updateMovie(@PathVariable Long id, @RequestBody Movie movie) {
        Optional<Movie> existing = movieService.findById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Movie m = existing.get();
        m.setTitle(movie.getTitle());
        m.setCategory(movie.getCategory());
        m.setPosterUrl(movie.getPosterUrl());
        Movie updated = movieService.save(m);
        return ResponseEntity.ok(updated);
    }

    @PutMapping(value = "/{id}", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Movie> updateMovieWithFile(
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam("category") String category,
            @RequestParam(value = "poster", required = false) MultipartFile poster) {
        Optional<Movie> optional = movieService.findById(id);
        if (optional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Movie existingMovie = optional.get();
        existingMovie.setTitle(title);
        existingMovie.setCategory(category);
        try {
            if (poster != null && !poster.isEmpty()) {
                // Garante diretório
                java.nio.file.Path uploadPath = java.nio.file.Paths.get(uploadDir);
                java.nio.file.Files.createDirectories(uploadPath);
                String originalFilename = poster.getOriginalFilename();
                String extension = "";
                if (originalFilename != null && originalFilename.contains(".")) {
                    extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                }
                String fileName = java.util.UUID.randomUUID() + extension;
                java.nio.file.Path filePath = uploadPath.resolve(fileName);
                poster.transferTo(filePath.toFile());
                existingMovie.setPosterUrl("/uploads/" + fileName);
            }
            Movie saved = movieService.save(existingMovie);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        movieService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}