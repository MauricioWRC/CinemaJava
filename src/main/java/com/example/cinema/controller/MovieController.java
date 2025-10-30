package com.example.cinema.controller;

import com.example.cinema.model.Movie;
import com.example.cinema.service.MovieService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;

/**
 * Controller REST responsável por expor as operações de CRUD para filmes.
 * Utiliza a camada de serviço para delegar as operações de negócio. Todos
 * endpoints retornam JSON.
 */
@RestController
@RequestMapping("/api/movies")
@CrossOrigin(origins = "*")
public class MovieController {
    private final MovieService movieService;
    // Diretório onde salvamos as imagens de cartaz
    private final String uploadDir;

    public MovieController(MovieService movieService, @Value("${upload.dir}") String uploadDir) {
        this.movieService = movieService;
        this.uploadDir = uploadDir;
    }

    /**
     * Retorna uma lista de filmes. Se a query string "category" for informada,
     * filtra os filmes por essa categoria. Exemplo de chamada:
     * GET /api/movies?category=Ação
     *
     * De acordo com as boas práticas de design de APIs REST, o uso de
     * parâmetros de URL para filtros simples é indicado【881580389030745†L55-L61】.
     *
     * @param category categoria opcional para filtrar
     * @return lista de filmes
     */
    @GetMapping
    public ResponseEntity<List<Movie>> listMovies(@RequestParam(required = false) String category) {
        List<Movie> movies = movieService.findByCategory(category);
        return ResponseEntity.ok(movies);
    }

    /**
     * Retorna um filme específico pelo id.
     *
     * @param id identificador do filme
     * @return filme encontrado ou 404 se não existir
     */
    @GetMapping("/{id}")
    public ResponseEntity<Movie> getMovie(@PathVariable Long id) {
        Optional<Movie> optionalMovie = movieService.findById(id);
        return optionalMovie.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Cria um novo filme. Para se adequar às convenções REST, esta operação
     * utiliza o método POST com corpo JSON contendo título, categoria e URL do cartaz【575354213323705†L96-L110】.
     *
     * @param movie filme enviado no corpo da requisição
     * @return filme salvo
     */
    @PostMapping
    public ResponseEntity<Movie> createMovie(@RequestBody Movie movie) {
        Movie saved = movieService.save(movie);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * Cria um filme recebendo dados multipart. Este método permite enviar um
     * arquivo de imagem (campo "poster") junto com os parâmetros texto (título e
     * categoria). O Spring Boot já está configurado para aceitar uploads
     * multi‑part【149768628369068†L134-L162】. O arquivo será salvo no diretório definido em
     * {@code upload.dir} e a propriedade posterUrl do filme será
     * preenchida com a URL acessível via /uploads/...
     *
     * @param title  título do filme
     * @param category categoria
     * @param poster arquivo da imagem do cartaz
     * @return filme salvo
     */
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

    /**
     * Atualiza um filme existente. Caso o filme não exista, responde 404.
     *
     * @param id    identificador do filme
     * @param movie dados para atualizar
     * @return filme atualizado ou 404
     */
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

    /**
     * Atualiza um filme existente permitindo enviar novo arquivo de cartaz. Caso
     * nenhum arquivo seja enviado, o cartaz antigo permanece. Esta versão
     * consome multipart/form-data.
     *
     * @param id       identificador do filme
     * @param title    título atualizado
     * @param category categoria atualizada
     * @param poster   arquivo de cartaz (opcional)
     * @return filme atualizado ou 404
     */
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

    /**
     * Exclui um filme pelo id. Responde 204 (No Content) mesmo se o filme não
     * existir para não expor informações desnecessárias.
     *
     * @param id identificador do filme
     * @return resposta sem conteúdo
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        movieService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}