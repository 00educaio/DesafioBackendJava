package caio_dev.Desafio_Livraria.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import caio_dev.Desafio_Livraria.dto.BookRequestDTO;
import caio_dev.Desafio_Livraria.dto.BookResponseDTO;
import caio_dev.Desafio_Livraria.entity.Book;
import caio_dev.Desafio_Livraria.repository.BookRepository;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {
    
    @SuppressWarnings("unchecked")
    private static Specification<Book> anyBookSpecification() {
        return any(Specification.class);
    }
    
    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @Test
    void testCreateBook() {
        BookRequestDTO request = new BookRequestDTO();
        request.setTitle("Test Book");
        request.setAuthor("Author");
        request.setPublicationYear(2020);
        request.setGenre("Fiction");

        Book savedBook = new Book();
        savedBook.setId(UUID.randomUUID());
        savedBook.setTitle("Test Book");

        when(bookRepository.save(any(Book.class))).thenReturn(savedBook);

        BookResponseDTO response = bookService.createBook(request);

        assertNotNull(response);
        assertEquals("Test Book", response.getTitle());
        verify(bookRepository, times(1)).save(any(Book.class));
    }
    

    @Test
    void testGetAllBooks() {
        Book book = new Book();
        book.setId(UUID.randomUUID());
        book.setTitle("Test Book");

        PageRequest pageable = PageRequest.of(0, 10);
        when(bookRepository.findAll(anyBookSpecification(), eq(pageable)))
                .thenReturn(new PageImpl<>(Arrays.asList(book)));

        var result = bookService.getAllBooks(pageable);

        assertEquals(1, result.getTotalElements());
        verify(bookRepository, times(1)).findAll(anyBookSpecification(), eq(pageable));
    }

    @Test
    void testDeleteBook() {
        UUID id = UUID.randomUUID();
        Book book = new Book();
        book.setId(id);
    
        when(bookRepository.findOne(anyBookSpecification())).thenReturn(Optional.of(book));
    
        bookService.deleteBook(id);
    
        assertTrue(book.isDeleted());
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void testDeleteBook_WhenBookNotFound_ShouldThrowException() {
        UUID id = UUID.randomUUID();

        when(bookRepository.findOne(anyBookSpecification())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> bookService.deleteBook(id)); // ou sua exceção personalizada

        verify(bookRepository, times(0)).save(any(Book.class));
    }
}