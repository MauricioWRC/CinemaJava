package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


public class DatabaseHelper {

    private static final String URL = "jdbc:sqlite:cinema.db";

    static {
        createTable();
    }

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    static void createTable() {
    	String createMovies = """
    			CREATE TABLE IF NOT EXISTS movies (
    			  id INTEGER PRIMARY KEY AUTOINCREMENT,
    			  title TEXT,
    			  category TEXT,
    			  posterUrl TEXT,   -- URL opcional
    			  idade INTEGER,
    			  duracao REAL,
    			  lancamento TEXT,
    			  posterMime TEXT,  -- MIME do arquivo
    			  posterBlob BLOB,  -- BLOB da imagem
    			  cinema_id INTEGER,
    			  FOREIGN KEY (cinema_id) REFERENCES cinemas(id) ON DELETE CASCADE
    			);
    			""";



        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(createMovies);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}