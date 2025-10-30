package model.db;

import java.sql.*;

public class DatabaseHelper {

    // Ativa FK na conexão
    private static final String URL = "jdbc:sqlite:cinema.db?foreign_keys=on";

    public static Connection connect() throws SQLException {
        Connection conn = DriverManager.getConnection(URL);
        // redundância saudável caso o driver ignore a query param
        try (Statement st = conn.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON");
        }
        return conn;
    }

    public static void createTables() {
        // Crie primeiro a "tabela pai"
        String createCinemas = """
            CREATE TABLE IF NOT EXISTS cinemas (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT,
                address TEXT,
                city TEXT,
                state TEXT,
                zipCode TEXT,
                phone TEXT
            );
        """;

        String createMovies = """
            CREATE TABLE IF NOT EXISTS movies (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT,
                category TEXT,
                posterUrl TEXT,
                cinema_id INTEGER NOT NULL,
                CONSTRAINT fk_movie_cinema
                    FOREIGN KEY (cinema_id)
                    REFERENCES cinemas(id)
                    ON DELETE CASCADE
            );
        """;

        // índices úteis
        String createIdx = """
            CREATE INDEX IF NOT EXISTS idx_movies_cinema_id ON movies (cinema_id);
        """;

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(createCinemas);
            stmt.execute(createMovies);
            stmt.execute(createIdx);
            System.out.println("Tabelas criadas com sucesso!");
        } catch (SQLException e) {
            System.err.println("Erro ao criar tabelas: " + e.getMessage());
        }
    }
}
