package model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Representa um filme no banco de dados. 
 * Cada instância possui um identificador único (id), um título, 
 * uma categoria e a URL do cartaz (posterUrl).
 */
@Entity
@Table(name = "movies")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long cinemaId; // ✅ FK direta para JDBC
    private String title;
    private String category;
    private String posterUrl;

    // ✅ Adicionando relacionamento N→1
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "cinema_id",              // nome da coluna FK
        nullable = false,                // obrigatório: todo filme precisa ter um cinema
        foreignKey = @ForeignKey(name = "fk_movie_cinema")
    )
    private Cinema cinema;

    public Movie() {}

    public Movie(Long id, String title, String category, String posterUrl) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.posterUrl = posterUrl;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }

    public Cinema getCinema() { return cinema; }
    public void setCinema(Cinema cinema) { this.cinema = cinema; }
    
    public Long getCinemaId() { return cinemaId; }
    public void setCinemaId(Long cinemaId) { this.cinemaId = cinemaId; }
}
