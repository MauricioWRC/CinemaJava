package com.example.cinema.repository;

import com.example.cinema.model.Movie;
import com.example.cinema.model.db.DatabaseHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovieDao {

    public Long insert(Movie m) {
        String sql = "INSERT INTO movies (title, category, posterUrl, cinema_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, m.getTitle());
            ps.setString(2, m.getCategory());
            ps.setString(3, m.getPosterUrl());
            ps.setLong(4, m.getCinemaId()); // FK obrigatória
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao inserir filme: " + e.getMessage());
        }
        return null;
    }

    public List<Movie> listByCinema(Long cinemaId) {
        List<Movie> out = new ArrayList<>();
        String sql = "SELECT * FROM movies WHERE cinema_id = ?";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, cinemaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Movie m = new Movie();
                    m.setId(rs.getLong("id"));
                    m.setTitle(rs.getString("title"));
                    m.setCategory(rs.getString("category"));
                    m.setPosterUrl(rs.getString("posterUrl"));
                    m.setCinemaId(rs.getLong("cinema_id"));
                    out.add(m);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar filmes: " + e.getMessage());
        }
        return out;
    }
}
