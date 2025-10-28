let slideIndex = 0;
const slides = document.querySelectorAll('.slide');
const prev = document.querySelector('.anterior');
const next = document.querySelector('.proximo');

function mostrarSlide(index) {
  slides.forEach(slide => slide.classList.remove('ativo'));
  slides[index].classList.add('ativo');
}

function proximoSlide() {
  slideIndex = (slideIndex + 1) % slides.length;
  mostrarSlide(slideIndex);
}

function anteriorSlide() {
  slideIndex = (slideIndex - 1 + slides.length) % slides.length;
  mostrarSlide(slideIndex);
}

next.addEventListener('click', proximoSlide);
prev.addEventListener('click', anteriorSlide);

// troca de  5 segundos em 5 segundos
setInterval(proximoSlide, 5000);

//Lista filme

const lista = document.getElementById('lista-filmes');
const setaDireita = document.getElementById('seta-direita');
const setaEsquerda = document.getElementById('seta-esquerda');

setaDireita.addEventListener('click', () => {
  lista.scrollBy({ left: 300, behavior: 'smooth' });
});

setaEsquerda.addEventListener('click', () => {
  lista.scrollBy({ left: -300, behavior: 'smooth' });
});

// ======== BUSCA DE FILMES ========
document.addEventListener("DOMContentLoaded", () => {
  const campoBusca = document.querySelector(".campo-busca");
  const botaoBusca = document.querySelector(".botao-busca");
  const filmes = document.querySelectorAll(".filme");

  function filtrarFilmes() {
    const termo = campoBusca.value.toLowerCase().trim();

    filmes.forEach(filme => {
      const titulo = filme.querySelector("h3").textContent.toLowerCase();
      // Mostra todos se o campo estiver vazio, ou só os que correspondem ao termo
      filme.style.display = termo === "" || titulo.includes(termo) ? "block" : "none";
    });
  }

  // Filtra ao clicar no botão
  botaoBusca.addEventListener("click", filtrarFilmes);

  // Filtra ao pressionar Enter ou enquanto digita
  campoBusca.addEventListener("keyup", event => {
    if (event.key === "Enter" || campoBusca.value === "") {
      filtrarFilmes();
    }
  });
});




//teste formulario


const form = document.getElementById('form-filme');

form.addEventListener('submit', (e) => {
  e.preventDefault();

  const filme = {
    nome: document.getElementById('nome').value,
    genero: document.getElementById('genero').value,
    idade: document.getElementById('idade').value,
    duracao: document.getElementById('duracao').value,
    lancamento: document.getElementById('lancamento').value,
    poster: document.getElementById('poster').value,
    sinopse: document.getElementById('sinopse').value
  };

  console.log('🎬 Novo filme cadastrado:', filme);
  alert(`Filme "${filme.nome}" cadastrado com sucesso!`);

  form.reset(); // limpa os campos
});
