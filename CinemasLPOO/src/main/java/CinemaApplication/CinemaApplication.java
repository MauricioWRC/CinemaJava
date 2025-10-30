package CinemaApplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import model.Cinema;
import model.Movie;
import model.db.DatabaseHelper;
import repository.CinemaDao;
import repository.MovieDao;


/**
 * Classe principal da aplicação. Ao executar, ela inicializa o Spring Boot
 * carregando todos os componentes, inclusive o servidor embutido Tomcat.
 */
@SpringBootApplication
public class CinemaApplication {
    public static void main(String[] args) {
        // cria tabelas se não existirem
        DatabaseHelper.createTables();

        CinemaDao cinemaDao = new CinemaDao();
        MovieDao movieDao = new MovieDao();

        // cria um cinema
        Cinema cine = new Cinema();
        cine.setName("LinkSchool Cine");
        Long cinemaId = cinemaDao.insert(cine);

        // cria filmes para esse cinema
        Movie m1 = new Movie();
        m1.setTitle("Oppenheimer");
        m1.setCategory("Drama");
        m1.setPosterUrl("https://.../opp.jpg");
        m1.setCinemaId(cinemaId);
        movieDao.insert(m1);

        Movie m2 = new Movie();
        m2.setTitle("Duna 2");
        m2.setCategory("Sci-Fi");
        m2.setPosterUrl("https://.../dune2.jpg");
        m2.setCinemaId(cinemaId);
        movieDao.insert(m2);

        // lista filmes do cinema
        movieDao.listByCinema(cinemaId)
                .forEach(f -> System.out.println(f.getId() + " - " + f.getTitle()));
                
        SpringApplication.run(CinemaApplication.class, args);
    }
}
