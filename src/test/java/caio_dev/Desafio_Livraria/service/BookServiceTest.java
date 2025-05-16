package caio_dev.Desafio_Livraria.service;

import caio_dev.Desafio_Livraria.dto.BookRequestDTO;
import caio_dev.Desafio_Livraria.dto.BookResponseDTO;
import caio_dev.Desafio_Livraria.entity.Book;
import caio_dev.Desafio_Livraria.service.BookService;
import caio_dev.Desafio_Livraria.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

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

        when(bookRepository.findAll(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(Arrays.asList(book)));

        var result = bookService.getAllBooks(PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        verify(bookRepository, times(1)).findAll(any(PageRequest.class));
    }

    @Test
    void testDeleteBook() {
        UUID id = UUID.randomUUID();
        Book book = new Book();
        book.setId(id);

        when(bookRepository.findById(id)).thenReturn(Optional.of(book));

        bookService.deleteBook(id);

        assertTrue(book.isDeleted());
        verify(bookRepository, times(1)).save(book);
    }
}