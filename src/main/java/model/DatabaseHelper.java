package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseHelper {

    // URL de conexão do H2
    // "jdbc:h2:./cinema" cria um arquivo chamado cinema.mv.db no diretório do projeto.
    private static final String URL = "jdbc:h2:./cinema";
    private static final String USER = "sa";      // usuário padrão
    private static final String PASSWORD = "";    // senha padrão (vazia)

    static {
        createTables();
    }

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    static void createTables() {
        String createCinemas = """
            CREATE TABLE IF NOT EXISTS cinemas (
                id IDENTITY PRIMARY KEY,
                name VARCHAR(255),
                location VARCHAR(255)
            );
        """;

        String createMovies = """
            CREATE TABLE IF NOT EXISTS movies (
                id IDENTITY PRIMARY KEY,
                title VARCHAR(255),
                category VARCHAR(255),
                posterUrl VARCHAR(500),
                idade INT,
                duracao DOUBLE,
                lancamento VARCHAR(50),
                posterMime VARCHAR(100),
                posterBlob BLOB,
                cinema_id BIGINT,
                FOREIGN KEY (cinema_id) REFERENCES cinemas(id) ON DELETE CASCADE
            );
        """;

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(createCinemas);
            stmt.execute(createMovies);
            System.out.println("Tabelas criadas com sucesso!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
