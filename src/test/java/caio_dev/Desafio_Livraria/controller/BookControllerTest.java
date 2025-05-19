/* package caio_dev.Desafio_Livraria.controller;

import caio_dev.Desafio_Livraria.repository.BookRepository;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "/api/books";
        bookRepository.deleteAll(); // Limpa o banco antes de cada teste
    }

    @Test
    void testGetAllBooks_ShouldReturnPageOfBooks() {
        given()
            .when()
            .get()
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("content", isA(java.util.List.class))
            .body("pageable", notNullValue());
    }

    @Test
    void testCreateBook_ShouldReturnCreatedBook() {
        given()
            .contentType("application/json; charset=UTF-8")
            .body("""
                {
                    "title": "Domain-Driven Design",
                    "author": "Eric Evans",
                    "publicationYear": 2003,
                    "genre": "Software Design"
                }
                """)
            .when()
            .post()
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .body("id", notNullValue())
            .body("title", equalTo("Domain-Driven Design"))
            .body("author", equalTo("Eric Evans"))
            .body("publicationYear", equalTo(2003))
            .body("genre", equalTo("Software Design"))
            .body("createdAt", notNullValue())
            .body("createdBy", notNullValue())
            .extract().path("id");
    }

    @Test
    void testCreateBooksBatch_ShouldReturnListOfCreatedBooks() {
        given()
            .contentType("application/json")
            .body("""
                {
                    "books": [
                        {
                            "title": "Clean Code",
                            "author": "Robert Martin",
                            "publicationYear": 2008,
                            "genre": "Programming"
                        },
                        {
                            "title": "Refactoring",
                            "author": "Martin Fowler",
                            "publicationYear": 1999,
                            "genre": "Programming"
                        }
                    ]
                }
                """)
            .when()
            .post("/batch")
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .body("size()", equalTo(2))
            .body("[0].title", equalTo("Clean Code"))
            .body("[1].title", equalTo("Refactoring"));
    }

    @Test
    void testUpdateBook_ShouldReturnUpdatedBook() {
        String bookId = given()
            .contentType("application/json")
            .body("""
                {
                    "title": "Original Title",
                    "author": "Original Author",
                    "publicationYear": 2000,
                    "genre": "Original Genre"
                }
                """)
            .when()
            .post()
            .then()
            .extract().path("id");

        given()
            .contentType("application/json")
            .body("""
                {
                    "title": "Updated Title",
                    "author": "Updated Author",
                    "publicationYear": 2020,
                    "genre": "Updated Genre"
                }
                """)
            .when()
            .put("/" + bookId)
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("id", equalTo(bookId))
            .body("title", equalTo("Updated Title"))
            .body("updatedAt", notNullValue());
    }

    @Test
    void testDeleteBook_ShouldReturnNoContent() {
        String bookId = given()
            .contentType("application/json")
            .body("""
                {
                    "title": "Book to Delete",
                    "author": "Author",
                    "publicationYear": 2020,
                    "genre": "Genre"
                }
                """)
            .when()
            .post()
            .then()
            .extract().path("id");

        given()
            .when()
            .delete("/" + bookId)
            .then()
            .statusCode(HttpStatus.NO_CONTENT.value());

        given()
            .when()
            .get("/" + bookId)
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void testGetBookById_ShouldReturnBook() {
        String bookId = given()
            .contentType("application/json")
            .body("""
                {
                    "title": "Specific Book",
                    "author": "Author",
                    "publicationYear": 2020,
                    "genre": "Genre"
                }
                """)
            .when()
            .post()
            .then()
            .extract().path("id");

        given()
            .when()
            .get("/" + bookId)
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("id", equalTo(bookId))
            .body("title", equalTo("Specific Book"));
    }

    @Test
    void testSearchBooks_ShouldReturnFilteredResults() {
        given()
            .contentType("application/json")
            .body("""
                {
                    "title": "Searchable Book",
                    "author": "Searchable Author",
                    "publicationYear": 2020,
                    "genre": "Test Genre"
                }
                """)
            .when()
            .post();

        given()
            .when()
            .get("/search?title=Searchable&genre=Test")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("content.size()", greaterThanOrEqualTo(1))
            .body("content[0].title", equalTo("Searchable Book"));
    }

    @Test
    void testGetBooksByGenreReport_ShouldReturnGenreStatistics() {
        given()
            .contentType("application/json")
            .body("""
                {
                    "books": [
                        {
                            "title": "Book 1",
                            "author": "Author 1",
                            "publicationYear": 2020,
                            "genre": "Fiction"
                        },
                        {
                            "title": "Book 2",
                            "author": "Author 2",
                            "publicationYear": 2021,
                            "genre": "Fiction"
                        },
                        {
                            "title": "Book 3",
                            "author": "Author 3",
                            "publicationYear": 2022,
                            "genre": "Non-Fiction"
                        }
                    ]
                }
                """)
            .when()
            .post("/batch");

        given()
            .when()
            .get("/report/genre")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("size()", greaterThanOrEqualTo(2))
            .body("find { it.genre == 'Fiction' }.count", equalTo(2))
            .body("find { it.genre == 'Non-Fiction' }.count", equalTo(1));
    }

    @Test
    void testGetNonExistentBook_ShouldReturnNotFound() {
        UUID randomId = UUID.randomUUID();
        given()
            .when()
            .get("/" + randomId)
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void testUpdateNonExistentBook_ShouldReturnNotFound() {
        UUID randomId = UUID.randomUUID();
        given()
            .contentType("application/json")
            .body("""
                {
                    "title": "Title",
                    "author": "Author",
                    "publicationYear": 2020,
                    "genre": "Genre"
                }
                """)
            .when()
            .put("/" + randomId)
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value());
    }
}
 */