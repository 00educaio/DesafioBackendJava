package caio_dev.Desafio_Livraria.service;

import caio_dev.Desafio_Livraria.dto.BatchBookRequestDTO;
import caio_dev.Desafio_Livraria.dto.BookRequestDTO;
import caio_dev.Desafio_Livraria.dto.BookResponseDTO;
import caio_dev.Desafio_Livraria.dto.GenreReportDTO;
import caio_dev.Desafio_Livraria.entity.Book;
import caio_dev.Desafio_Livraria.repository.BookRepository;
import caio_dev.Desafio_Livraria.repository.BookSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;

    public BookResponseDTO createBook(BookRequestDTO request) {
        Book book = new Book();
        mapRequestToEntity(request, book);
        book.setCreatedBy(getCurrentUserId());
        Book savedBook = bookRepository.save(book);
        return mapToResponseDTO(savedBook);
    }

    public List<BookResponseDTO> createBooksBatch(BatchBookRequestDTO batchRequest) {
        List<Book> books = batchRequest.books().stream()
                .map(request -> {
                    Book book = new Book();
                    mapRequestToEntity(request, book);
                    book.setCreatedBy(getCurrentUserId());
                    return book;
                })
                .toList();
        List<Book> savedBooks = bookRepository.saveAll(books);
        return savedBooks.stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    public BookResponseDTO updateBook(UUID id, BookRequestDTO request) {
        Book book = bookRepository.findOne(BookSpecifications.notDeleted().and((root, query, cb) -> cb.equal(root.get("id"), id)))
                .orElseThrow(() -> new RuntimeException("Book not found"));
        mapRequestToEntity(request, book);
        book.setUpdatedBy(getCurrentUserId());
        Book updatedBook = bookRepository.save(book);
        return mapToResponseDTO(updatedBook);
    }

    public void deleteBook(UUID id) {
        Book book = bookRepository.findOne(BookSpecifications.notDeleted().and((root, query, cb) -> cb.equal(root.get("id"), id)))
                .orElseThrow(() -> new RuntimeException("Book not found"));
        book.setDeleted(true);
        book.setUpdatedBy(getCurrentUserId());
        bookRepository.save(book);
    }

    public BookResponseDTO getBookById(UUID id) {
        Book book = bookRepository.findOne(BookSpecifications.notDeleted().and((root, query, cb) -> cb.equal(root.get("id"), id)))
                .orElseThrow(() -> new RuntimeException("Book not found"));
        return mapToResponseDTO(book);
    }

    public Page<BookResponseDTO> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(BookSpecifications.notDeleted(), pageable)
                .map(this::mapToResponseDTO);
    }

    public Page<BookResponseDTO> searchBooks(String title, String author, String genre, Integer publicationYear, Pageable pageable) {
        return bookRepository.findAll(BookSpecifications.searchBooks(title, author, genre, publicationYear), pageable)
                .map(this::mapToResponseDTO);
    }

    public List<GenreReportDTO> getBooksByGenreReport() {
        List<Book> books = bookRepository.findAll(BookSpecifications.notDeleted());
        Map<String, Long> genreCount = books.stream()
                .filter(book -> !book.isDeleted()) // Filtro redundante, mas mantido para clareza
                .collect(Collectors.groupingBy(
                        book -> book.getGenre() != null ? book.getGenre() : "Unknown",
                        Collectors.counting()
                ));
        return genreCount.entrySet().stream()
                .map(entry -> {
                    GenreReportDTO dto = new GenreReportDTO();
                    dto.setGenre(entry.getKey());
                    dto.setCount(entry.getValue());
                    return dto;
                })
                .sorted(Comparator.comparing(GenreReportDTO::getGenre))
                .collect(Collectors.toList());
    }

    private void mapRequestToEntity(BookRequestDTO request, Book book) {
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setPublicationYear(request.getPublicationYear());
        book.setGenre(request.getGenre());
    }

    private BookResponseDTO mapToResponseDTO(Book book) {
        BookResponseDTO response = new BookResponseDTO();
        response.setId(book.getId());
        response.setTitle(book.getTitle());
        response.setAuthor(book.getAuthor());
        response.setPublicationYear(book.getPublicationYear());
        response.setGenre(book.getGenre());
        response.setCreatedAt(book.getCreatedAt());
        response.setUpdatedAt(book.getUpdatedAt());
        response.setCreatedBy(book.getCreatedBy());
        response.setUpdatedBy(book.getUpdatedBy());
        return response;
    }

    private UUID getCurrentUserId() {
        // Implementar lógica para obter o ID do usuário autenticado
        return UUID.randomUUID(); // Placeholder
    }
}