package com.example.cinema.repository;

import com.example.cinema.model.Cinema;
import com.example.cinema.model.db.DatabaseHelper;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CinemaDao {

    public Long insert(Cinema c) {
        String sql = "INSERT INTO cinemas (name, address, city, state, zipCode, phone) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, c.getName());
            ps.setString(2, c.getAddress());
            ps.setString(3, c.getCity());
            ps.setString(4, c.getState());
            ps.setString(5, c.getZipCode());
            ps.setString(6, c.getPhone());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao inserir cinema: " + e.getMessage());
        }
        return null;
    }

    public List<Cinema> listAll() {
        List<Cinema> out = new ArrayList<>();
        String sql = "SELECT * FROM cinemas";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Cinema c = new Cinema();
                c.setId(rs.getLong("id"));
                c.setName(rs.getString("name"));
                c.setAddress(rs.getString("address"));
                c.setCity(rs.getString("city"));
                c.setState(rs.getString("state"));
                c.setZipCode(rs.getString("zipCode"));
                c.setPhone(rs.getString("phone"));
                out.add(c);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar cinemas: " + e.getMessage());
        }
        return out;
    }
}
