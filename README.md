CinemaJava (branch dev)

Este projeto é um sistema de gestão de filmes para cinemas, escrito em Java 17 com Spring Boot 3. Ele oferece uma API REST para cadastrar, listar, editar e excluir filmes de um cinema, bem como um painel web simples para administração. Há também um mecanismo de cadastro/autenticação de usuários e tratamento de upload de imagens de cartaz.

Visão geral

O código do backend encontra‑se em CinemasLPOO/src/main/java e é estruturado em camadas:

CinemaApplication – diretório com a classe CinemaApplication.java, que inicializa a aplicação Spring Boot e cria algumas entradas de exemplo no banco de dados.

model – contém as entidades JPA:

Cinema – representa um cinema; possui campos como nome, endereço, cidade, estado, CEP, telefone e uma lista de filmes (relacionamento 1→N).

Movie – representa um filme; possui título, categoria, URL do cartaz e relação N→1 com o cinema
github.com
. Também guarda o campo cinemaId quando utilizado via JDBC.

User – representa um usuário com username e password
github.com
.

model/db/DatabaseHelper – utilitário para criar um banco SQLite (cinema.db) e as tabelas cinemas e movies com chave estrangeira e índices
github.com
.

repository – camadas de persistência:

Repositórios JPA como MovieRepository e UserRepository, que estendem JpaRepository para operações CRUD e incluem consultas como findByCategory
github.com
 e findByUsername
github.com
.

DAOs JDBC (CinemaDao, MovieDao) que executam consultas e inserções diretamente no banco usando DatabaseHelper
github.com
github.com
.

service – contém a lógica de negócio. Há serviços como MovieService e UserService que invocam os repositórios. (Observe que alguns métodos fazem casts incorretos; o ideal é chamar diretamente o repositório.)

controller – expõe a API REST:

MovieController – mapeado em /api/movies. Permite listar todos os filmes (com filtro opcional por categoria)
github.com
, buscar por id
github.com
, cadastrar filmes (JSON ou multipart para upload de imagem)
github.com
, atualizar e deletar filmes
github.com
.

AuthController – mapeado em /api/auth. Implementa login simples e cadastro de novos usuários. O administrador padrão é admin/1234; usuários adicionais são salvos no banco
github.com
. O endpoint /login devolve 200 OK se as credenciais forem válidas
github.com
; /register cria um novo usuário ou retorna 409 Conflict se já existir
github.com
.

config – contém WebConfig para mapear o diretório de uploads para a URL /uploads/**
github.com
.

Arquivos de configuração estão em CinemasLPOO/src/main/resources:

application.properties define a conexão com o banco H2 em memória (usado pelo Spring Data JPA), habilita o log SQL e aponta a propriedade upload.dir (diretório onde as imagens de cartaz são gravadas)
github.com
.

O diretório static/ contém páginas HTML e CSS do front‑end, por exemplo register.html, admin.html e admin.css. A página admin.html implementa um painel onde o administrador pode adicionar, editar ou excluir filmes via chamadas à API
github.com
.

Pré‑requisitos

Java 17 ou superior

Maven 3.8+

Configuração e execução

Clonar o repositório e acessar a branch dev:

git clone https://github.com/MauricioWRC/CinemaJava.git
cd CinemaJava
git checkout dev


Compilar o projeto:

mvn clean install


Executar a aplicação:

Pela linha de comando:

mvn spring-boot:run


Ou executando a classe CinemaApplication dentro de uma IDE.

Acessar o sistema:

A API estará disponível em http://localhost:8080.

O painel de administração pode ser acessado abrindo src/main/resources/static/admin.html em um navegador (use um servidor estático ou configure Spring para servir os recursos). Ele solicitará login; use admin/1234 ou cadastre um novo usuário.

Endpoints principais
Método	Endpoint	Descrição
GET	/api/movies	Lista todos os filmes; aceita category como filtro opcional.
GET	/api/movies/{id}	Busca detalhes de um filme específico.
POST	/api/movies	Cadastra um novo filme via JSON (campos title, category, posterUrl).
POST	/api/movies (multipart)	Cadastra um filme com upload de arquivo (campos title, category, poster).
PUT	/api/movies/{id}	Atualiza um filme existente (JSON ou multipart).
DELETE	/api/movies/{id}	Remove um filme.
POST	/api/auth/login	Realiza login; corpo JSON com username e password.
POST	/api/auth/register	Registra um novo usuário.
Estrutura do diretório
CinemaJava/
└── CinemasLPOO/
    ├── pom.xml               # Configuração Maven
    └── src/main/
        ├── java/
        │   ├── CinemaApplication/      # classe principal
        │   ├── config/                 # configuração Web MVC
        │   ├── controller/             # controladores REST
        │   ├── model/                  # entidades de domínio e db helper
        │   ├── repository/             # repositórios JPA e DAOs JDBC
        │   └── service/                # camada de serviço
        ├── resources/
        │   ├── application.properties  # propriedades Spring
        │   └── static/                 # páginas HTML e CSS
        └── webapp/                     # páginas Web (alternativa à pasta static)

Observações

A aplicação usa Spring Data JPA com H2 para persistência em memória durante o desenvolvimento. É possível trocar para outro banco (MySQL, PostgreSQL) ajustando spring.datasource.* e spring.jpa.hibernate.ddl-auto em application.properties.

O utilitário DatabaseHelper cria um banco SQLite e contém métodos para inserção via JDBC. Essa abordagem coexiste com o JPA; escolha uma estratégia única de persistência para evitar inconsistências.

Para upload de imagens, o diretório definido em upload.dir deve existir ou será criado automaticamente. As imagens ficam acessíveis através do path /uploads/.

Este README resume a branch dev do projeto, explicando a estrutura, tecnologias e instruções de uso. Sinta‑se à vontade para melhorá‑lo ou traduzir para outro idioma conforme necessário.
