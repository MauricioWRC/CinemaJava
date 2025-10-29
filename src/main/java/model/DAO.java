package model;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.*;

public class DAO {

    public DAO() {
        // garante que a tabela exista
        DatabaseHelper.createTable();
    }

    /** INSERT com BLOB opcional */
    public void addMovie(JavaBeans m, InputStream posterIn, String mime) throws SQLException, IOException {
        String sql = "INSERT INTO movies (title, category, posterUrl, idade, duracao, lancamento, posterMime, posterBlob) " +
                     "VALUES (?,?,?,?,?,?,?,?)";
        try (Connection c = DatabaseHelper.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, m.getNome());
            ps.setString(2, m.getGenero());
            ps.setString(3, m.getPoster()); // URL opcional
            ps.setInt(4, m.getIdade());
            ps.setFloat(5, m.getDuracao());
            ps.setString(6, m.getLancamento());

            if (posterIn != null) {
                ps.setString(7, mime);
                ps.setBytes(8, posterIn.readAllBytes());
            } else {
                ps.setNull(7, Types.VARCHAR);
                ps.setNull(8, Types.BLOB);
            }

            ps.executeUpdate();
        }
    }

    /** UPDATE: troca BLOB só se um novo arquivo for enviado */
    public void updateMovie(JavaBeans m, InputStream posterIn, String mime) throws SQLException, IOException {
        boolean withBlob = (posterIn != null);
        String sql = withBlob
                ? "UPDATE movies SET title=?, category=?, posterUrl=?, idade=?, duracao=?, lancamento=?, posterMime=?, posterBlob=? WHERE id=?"
                : "UPDATE movies SET title=?, category=?, posterUrl=?, idade=?, duracao=?, lancamento=? WHERE id=?";

        try (Connection c = DatabaseHelper.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, m.getNome());
            ps.setString(2, m.getGenero());
            ps.setString(3, m.getPoster());
            ps.setInt(4, m.getIdade());
            ps.setFloat(5, m.getDuracao());
            ps.setString(6, m.getLancamento());

            int idx = 7;
            if (withBlob) {
                ps.setString(idx++, mime);
                ps.setBytes(idx++, posterIn.readAllBytes());
            }
            ps.setInt(idx, Integer.parseInt(m.getIdcon()));
            ps.executeUpdate();
        }
    }

    public void deleteMovie(int id) throws SQLException {
        try (Connection c = DatabaseHelper.connect();
             PreparedStatement ps = c.prepareStatement("DELETE FROM movies WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    /** Listagem simples em JavaBeans (útil para HTML no servidor) */
    public List<JavaBeans> listMovies() throws SQLException {
        String sql = "SELECT id, title, category, posterUrl, idade, duracao, lancamento FROM movies ORDER BY id DESC";
        List<JavaBeans> out = new ArrayList<>();
        try (Connection c = DatabaseHelper.connect();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                JavaBeans m = new JavaBeans();
                m.setIdcon(String.valueOf(rs.getInt("id")));
                m.setNome(rs.getString("title"));
                m.setGenero(rs.getString("category"));
                m.setPoster(rs.getString("posterUrl")); // pode ser null
                m.setIdade(rs.getInt("idade"));
                m.setDuracao(rs.getFloat("duracao"));
                m.setLancamento(rs.getString("lancamento"));
                out.add(m);
            }
        }
        return out;
    }

    /** Listagem para a API: inclui flag de BLOB */
    public List<Map<String, Object>> listMoviesApi() throws SQLException {
        String sql = """
            SELECT id, title, category, posterUrl, idade, duracao, lancamento,
                   CASE WHEN posterBlob IS NOT NULL THEN 1 ELSE 0 END AS hasBlob
            FROM movies
            ORDER BY id DESC
        """;
        List<Map<String, Object>> list = new ArrayList<>();
        try (Connection c = DatabaseHelper.connect();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("id", rs.getInt("id"));
                row.put("nome", rs.getString("title"));
                row.put("genero", rs.getString("category"));
                row.put("idade", rs.getInt("idade"));
                row.put("duracao", rs.getFloat("duracao"));
                row.put("lancamento", rs.getString("lancamento"));
                row.put("poster", rs.getString("posterUrl")); // URL original (se houver)
                row.put("hasBlob", rs.getInt("hasBlob") == 1);
                list.add(row);
            }
        }
        return list;
    }
}
