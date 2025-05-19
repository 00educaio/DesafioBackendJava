/* package caio_dev.Desafio_Livraria.service;

import caio_dev.Desafio_Livraria.dto.BookRequestDTO;
import caio_dev.Desafio_Livraria.dto.BookResponseDTO;
import caio_dev.Desafio_Livraria.entity.Book;
import caio_dev.Desafio_Livraria.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    private Book book;
    private BookRequestDTO bookRequestDTO;
    private UUID bookId;
    private UUID userId;

    // Método helper para evitar warnings
    @SuppressWarnings("unchecked")
    private static Specification<Book> anyBookSpecification() {
        return any(Specification.class);
    }

    @BeforeEach
    void setUp() {
        bookId = UUID.randomUUID();
        userId = UUID.randomUUID();

        book = new Book();
        book.setId(bookId);
        book.setTitle("Clean Code");
        book.setAuthor("Robert C. Martin");
        book.setPublicationYear(2008);
        book.setGenre("Programming");
        book.setCreatedAt(LocalDateTime.now());
        book.setUpdatedAt(LocalDateTime.now());
        book.setCreatedBy(userId);
        book.setUpdatedBy(userId);
        book.setDeleted(false);

        bookRequestDTO = new BookRequestDTO();
        bookRequestDTO.setTitle("Clean Code");
        bookRequestDTO.setAuthor("Robert C. Martin");
        bookRequestDTO.setPublicationYear(2008);
        bookRequestDTO.setGenre("Programming");
    }

    // ... outros métodos de teste permanecem iguais ...

    @Test
    void updateBook_WhenBookExists_ShouldReturnUpdatedBookResponseDTO() {
        when(bookRepository.findOne(anyBookSpecification())).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        BookResponseDTO response = bookService.updateBook(bookId, bookRequestDTO);

        assertNotNull(response);
        assertEquals(book.getTitle(), response.getTitle());
        verify(bookRepository, times(1)).findOne(anyBookSpecification());
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void getAllBooks_ShouldReturnPageOfBookResponseDTO() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> bookPage = new PageImpl<>(List.of(book), pageable, 1);
        
        when(bookRepository.findAll(anyBookSpecification(), any(Pageable.class))).thenReturn(bookPage);

        Page<BookResponseDTO> response = bookService.getAllBooks(pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals(book.getTitle(), response.getContent().get(0).getTitle());
        verify(bookRepository, times(1)).findAll(anyBookSpecification(), any(Pageable.class));
    }

    // ... atualize todos os outros métodos que usam Specification ...
} */