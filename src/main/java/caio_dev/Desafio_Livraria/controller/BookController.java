package caio_dev.Desafio_Livraria.controller;


import caio_dev.Desafio_Livraria.dto.BatchBookRequestDTO;
import caio_dev.Desafio_Livraria.dto.BookRequestDTO;
import caio_dev.Desafio_Livraria.dto.BookResponseDTO;
import caio_dev.Desafio_Livraria.dto.GenreReportDTO;
import caio_dev.Desafio_Livraria.service.BookService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor //Anotação do Lombok pra poder usar o final
public class BookController {
    private final BookService bookService; //Servico que contém a lógica, o controller delega as operações para o BookService

    @PostMapping
    @PreAuthorize("hasRole('USER')") //Autenticação com role user do SecurityConfig
    @SecurityRequirement(name = "bearerAuth") //Autenticação do SwaggerConfig
    public ResponseEntity<BookResponseDTO> createBook(@Valid @RequestBody BookRequestDTO request) {
        return ResponseEntity.ok(bookService.createBook(request));                                  //Criação do livro passando pelo dto com as validações e os metodos do BookService
    }

    @PostMapping("/batch")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<BookResponseDTO>> createBooksBatch(@Valid @RequestBody BatchBookRequestDTO request) {
        return ResponseEntity.ok(bookService.createBooksBatch(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BookResponseDTO> updateBook(@PathVariable UUID id, @Valid @RequestBody BookRequestDTO request) {
        return ResponseEntity.ok(bookService.updateBook(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> deleteBook(@PathVariable UUID id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponseDTO> getBookById(@PathVariable UUID id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @GetMapping
    public ResponseEntity<Page<BookResponseDTO>> getAllBooks(Pageable pageable) {
        return ResponseEntity.ok(bookService.getAllBooks(pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<BookResponseDTO>> searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) Integer publicationYear,
            Pageable pageable) {
        return ResponseEntity.ok(bookService.searchBooks(title, author, genre, publicationYear, pageable));
    }

    @GetMapping("/report/genres")
    public ResponseEntity<List<GenreReportDTO>> getBooksByGenreReport() {
        return ResponseEntity.ok(bookService.getBooksByGenreReport());
    }
}