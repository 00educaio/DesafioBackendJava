# Testar os endpoint -ok
1. getAll -ok
2. getOne -ok
3. post - ok
4. put -ok
5. delete(softDelete) -ok 
6. search -ok
7. genreReport -ok

1. CRUD de livros, com soft delete via Hibernate (não remover fisicamente do
banco).
# (BookService, BookSpecification)

2. Endpoint público: listagem de livros (sem autenticação) e paginado.
    a. Busca com parâmetros opcionais ( title , author , genre , publicationYear )
3. Endpoint autenticado: criação, edição e deleção. 
# (BookController)

4. Liquibase: criação das tabelas.
# (db.changelog-master.yaml e as changes)

5. Docker: containerizar o app + PostgreSQL para rodar com docker-compose .
# (compose.yml)

6. Relatório: endpoint que retorna a quantidade de livros por gênero. O agrupamento deverá ser feito via código (não delegar ao banco) e a possibilidade de um grande volume de dados deve ser considerada.
# (BookService(getBooksByGenreReport))

7. Validações dos dados de entrada (e.x. título obrigatório, ano > 0).
# (BookRequestDTOs, JacksonConfig)

8. A API deve estar documentada (com Swagger).
# (SecurityConfig)

9. Testes unitários
# (Bixo de sete cabeças)