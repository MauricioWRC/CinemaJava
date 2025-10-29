package controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.*;
import model.DatabaseHelper;

@WebServlet("/image")
public class ImageServlet extends HttpServlet {
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String id = req.getParameter("id");
    if (id == null) { resp.sendError(400, "id obrigatório"); return; }
    String sql = "SELECT posterMime, posterBlob FROM movies WHERE id = ?";
    try (Connection c = DatabaseHelper.connect();
         PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setInt(1, Integer.parseInt(id));
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          byte[] blob = rs.getBytes("posterBlob");
          String mime = rs.getString("posterMime");
          if (blob != null && blob.length > 0) {
            resp.setContentType(mime != null ? mime : "image/jpeg");
            resp.setContentLength(blob.length);
            resp.getOutputStream().write(blob);
            return;
          }
        }
      }
    } catch (Exception e) {
      resp.sendError(500, e.getMessage());
      return;
    }
    resp.sendError(404);
  }
}
