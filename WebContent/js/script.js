// WebContent/js/script.js
(function () {
  // ======= HOME: Banner =======
  function initBanner() {
    var slides = document.querySelectorAll(".banner .slide");
    if (!slides.length) return;
    var idx = 0, btnPrev = document.querySelector(".banner .anterior"), btnNext = document.querySelector(".banner .proximo"), timer;
    function show(i){ for (var k=0;k<slides.length;k++) slides[k].classList.remove("ativo"); slides[i].classList.add("ativo"); }
    function next(){ idx = (idx + 1) % slides.length; show(idx); }
    function prev(){ idx = (idx - 1 + slides.length) % slides.length; show(idx); }
    function restart(){ if (timer) clearInterval(timer); timer = setInterval(next, 5000); }
    if (btnPrev) btnPrev.addEventListener("click", function(){ prev(); restart(); });
    if (btnNext) btnNext.addEventListener("click", function(){ next(); restart(); });
    show(idx); restart();
  }

  // ======= HOME: Carrossel =======
  function initCarrossel() {
    var lista = document.getElementById("lista-filmes");
    var esq = document.getElementById("seta-esquerda");
    var dir = document.getElementById("seta-direita");
    if (!lista || !esq || !dir) return;
    function scrollByCards(direction) {
      var card = lista.querySelector(".filme");
      var step = card ? (card.offsetWidth + 14) * 2 : 300;
      lista.scrollBy({ left: direction * step, behavior: "smooth" });
    }
    esq.addEventListener("click", function(){ scrollByCards(-1); });
    dir.addEventListener("click", function(){ scrollByCards(1); });
  }

  // ======= ADMIN: CRUD + upload =======
  function initAdminCrud() {
    var form = document.getElementById("movie-form");
    var tableBody = document.querySelector("#movies-table tbody");
    var cancelBtn = document.getElementById("cancel-btn");
    var saveBtn = document.getElementById("save-btn");
    if (!form || !tableBody) return; // não é a página admin

    var API_BASE = "main";

    function fetchJSON(url, options) {
      options = options || {};
      return fetch(url, options).then(function (res) {
        if (!res.ok) throw new Error("HTTP " + res.status);
        return res.json();
      });
    }

    function loadMovies() {
      return fetchJSON(API_BASE + "?acao=listar&format=json")
        .then(function (data) { renderRows(data); })
        .catch(function (err) { alert("Erro ao listar filmes: " + err.message); });
    }

    function imgTagFor(m) {
      // Se tem BLOB salvo, o backend retorna posterUrl = "image?id=<id>"
      var src = m.posterUrl ? m.posterUrl : (m.poster ? m.poster : "");
      return src ? '<img src="' + src + '" alt="poster" style="height:56px;width:auto;border-radius:6px;" />' : "";
    }

    function renderRows(movies) {
      tableBody.innerHTML = "";
      movies.forEach(function (m) {
        var tr = document.createElement("tr");
        tr.innerHTML =
          "<td>" + (m.id != null ? m.id : "") + "</td>" +
          "<td>" + imgTagFor(m) + "</td>" +
          "<td>" + esc(m.nome || "") + "</td>" +
          "<td>" + esc(m.genero || "") + "</td>" +
          "<td>" + (m.idade != null ? m.idade : "") + "</td>" +
          "<td>" + (m.duracao != null ? m.duracao : "") + "</td>" +
          "<td>" + esc(m.lancamento || "") + "</td>" +
          '<td class="actions-cell">' +
            '<button class="edit-btn">Editar</button>' +
            '<button class="danger delete-btn">Excluir</button>' +
          "</td>";
        tableBody.appendChild(tr);

        tr.querySelector(".edit-btn").addEventListener("click", function () { startEdit(m); });
        tr.querySelector(".delete-btn").addEventListener("click", function () { handleDelete(m.id); });
      });
    }

    function startEdit(m) {
      setVal("id", m.id != null ? m.id : "");
      setVal("nome", m.nome || "");
      setVal("genero", m.genero || "");
      setVal("idade", m.idade != null ? m.idade : "");
      setVal("duracao", m.duracao != null ? m.duracao : "");
      setVal("lancamento", m.lancamento || "");
      setVal("poster", m.poster || "");
      // não preenche file (por segurança do browser)
      if (saveBtn) saveBtn.textContent = "Atualizar";
    }

    function setVal(id, v) { var el = document.getElementById(id); if (el) el.value = v; }

    function resetForm() {
      form.reset();
      setVal("id", "");
      if (saveBtn) saveBtn.textContent = "Salvar";
    }

    function handleDelete(id) {
      if (!id || !confirm("Excluir este filme?")) return;
      var fd = new FormData(); fd.append("id", id);
      fetch(API_BASE + "?acao=excluir", { method: "POST", body: fd })
        .then(function (res) { if (!res.ok) throw new Error("HTTP " + res.status); return loadMovies(); })
        .catch(function (err) { alert("Erro ao excluir: " + err.message); });
    }

    form.addEventListener("submit", function (e) {
      e.preventDefault();
      var fd = new FormData(form); // contém campos + arquivo "posterFile" (se escolhido)
      var isUpdate = !!fd.get("id");
      var acao = isUpdate ? "atualizar" : "inserir";
      fetch(API_BASE + "?acao=" + acao, { method: "POST", body: fd })
        .then(function (res) {
          if (!res.ok) return res.text().then(function (t) { throw new Error(t || ("HTTP " + res.status)); });
          resetForm();
          return loadMovies();
        })
        .catch(function (err) { alert("Erro ao salvar: " + err.message); });
    });

    if (cancelBtn) cancelBtn.addEventListener("click", resetForm);
    loadMovies();
  }

  function esc(s) {
    if (!s) return "";
    return String(s).replace(/&/g,"&amp;").replace(/</g,"&lt;").replace(/>/g,"&gt;").replace(/\"/g,"&quot;").replace(/'/g,"&#039;");
  }

  document.addEventListener("DOMContentLoaded", function () {
    initBanner();
    initCarrossel();
    initAdminCrud();
  });
})();
