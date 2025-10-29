package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.*;

import model.DAO;
import model.JavaBeans;

@WebServlet(urlPatterns = {"/Controller", "/main"})
@MultipartConfig(maxFileSize = 5 * 1024 * 1024) // 5MB
public class Controller extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private DAO dao;

    @Override
    public void init() throws ServletException {
        super.init();
        dao = new DAO(); // garante createTables()
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String acao = opt(req.getParameter("acao"), "listar");
        String format = opt(req.getParameter("format"), "html");

        if ("listar".equalsIgnoreCase(acao)) {
            try {
                if ("json".equalsIgnoreCase(format)) {
                    var list = dao.listMoviesApi();
                    resp.setContentType("application/json; charset=UTF-8");
                    try (PrintWriter out = resp.getWriter()) {
                        out.print(toJson(list));
                    }
                } else {
                    // HTML simples (útil pra debug)
                    var list = dao.listMovies();
                    resp.setContentType("text/html; charset=UTF-8");
                    try (PrintWriter out = resp.getWriter()) {
                        out.println("<!DOCTYPE html><html><head><meta charset='UTF-8'><title>Filmes</title></head><body>");
                        out.println("<h2>Filmes</h2><table border='1'><tr><th>ID</th><th>Nome</th><th>Gênero</th><th>Idade</th><th>Duração</th><th>Lançamento</th></tr>");
                        for (JavaBeans m : list) {
                            out.println("<tr><td>" + safe(m.getIdcon()) + "</td><td>" + safe(m.getNome()) + "</td><td>" +
                                    safe(m.getGenero()) + "</td><td>" + m.getIdade() + "</td><td>" +
                                    m.getDuracao() + "</td><td>" + safe(m.getLancamento()) + "</td></tr>");
                        }
                        out.println("</table><p><a href='admin.html'>Admin</a></p></body></html>");
                    }
                }
                return;
            } catch (Exception e) {
                resp.sendError(500, e.getMessage());
                return;
            }
        }

        resp.sendError(400, "Ação GET não suportada");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String acao = opt(req.getParameter("acao"), "");

        try {
            switch (acao) {
                case "inserir": {
                    JavaBeans m = fromRequest(req);
                    Part file = safePart(req, "posterFile");
                    InputStream posterIn = (file != null && file.getSize() > 0) ? file.getInputStream() : null;
                    String mime = (file != null && file.getSize() > 0) ? file.getContentType() : null;

                    dao.addMovie(m, posterIn, mime);
                    resp.setStatus(204); // No Content
                    break;
                }
                case "atualizar": {
                    JavaBeans m = fromRequest(req);
                    if (isEmpty(m.getIdcon())) {
                        resp.sendError(400, "ID obrigatório para atualizar");
                        return;
                    }
                    Part file = safePart(req, "posterFile");
                    InputStream posterIn = (file != null && file.getSize() > 0) ? file.getInputStream() : null;
                    String mime = (file != null && file.getSize() > 0) ? file.getContentType() : null;

                    dao.updateMovie(m, posterIn, mime);
                    resp.setStatus(204);
                    break;
                }
                case "excluir": {
                    String id = req.getParameter("id");
                    if (isEmpty(id)) {
                        resp.sendError(400, "ID obrigatório para excluir");
                        return;
                    }
                    dao.deleteMovie(Integer.parseInt(id));
                    resp.setStatus(204);
                    break;
                }
                default:
                    resp.sendError(400, "Ação POST não suportada");
            }
        } catch (Exception e) {
            resp.sendError(500, e.getMessage());
        }
    }

    // ---------- helpers ----------
    private JavaBeans fromRequest(HttpServletRequest req) {
        JavaBeans m = new JavaBeans();
        m.setIdcon(req.getParameter("id"));
        m.setNome(req.getParameter("nome"));
        m.setGenero(req.getParameter("genero"));
        m.setIdade(parseInt(req.getParameter("idade")));
        m.setDuracao(parseFloat(req.getParameter("duracao")));
        m.setLancamento(req.getParameter("lancamento"));
        m.setPoster(req.getParameter("poster")); // URL opcional
        return m;
    }

    private String toJson(List<Map<String, Object>> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> r = list.get(i);
            int id = (Integer) r.get("id");
            String posterOriginal = nvl((String) r.get("poster"));
            boolean hasBlob = (Boolean) r.get("hasBlob");
            String posterUrl = hasBlob ? ("image?id=" + id) : posterOriginal;

            sb.append("{")
              .append("\"id\":").append(id).append(',')
              .append("\"nome\":\"").append(esc(nvl((String) r.get("nome")))).append("\",")
              .append("\"genero\":\"").append(esc(nvl((String) r.get("genero")))).append("\",")
              .append("\"idade\":").append(((Number) r.get("idade")).intValue()).append(',')
              .append("\"duracao\":").append(((Number) r.get("duracao")).floatValue()).append(',')
              .append("\"lancamento\":\"").append(esc(nvl((String) r.get("lancamento")))).append("\",")
              .append("\"poster\":\"").append(esc(posterOriginal)).append("\",")
              .append("\"posterUrl\":\"").append(esc(posterUrl)).append("\"")
              .append("}");
            if (i < list.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    private Part safePart(HttpServletRequest req, String name) {
        try { return req.getPart(name); } catch (Exception e) { return null; }
    }

    private String opt(String v, String d) { return v == null ? d : v; }
    private boolean isEmpty(String s) { return s == null || s.isEmpty(); }
    private int parseInt(String s) { try { return Integer.parseInt(s); } catch (Exception e) { return 0; } }
    private float parseFloat(String s) { try { return Float.parseFloat(s); } catch (Exception e) { return 0f; } }
    private String nvl(String s) { return s == null ? "" : s; }
    private String safe(String s) { return s == null ? "" : s; }
    private String esc(String s) { return s.replace("\\","\\\\").replace("\"","\\\""); }
}
