package caio_dev.Desafio_Livraria.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification; // O Warning não é um erro, funciona certinho.
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import caio_dev.Desafio_Livraria.dto.BatchBookRequestDTO;
import caio_dev.Desafio_Livraria.dto.BookRequestDTO;
import caio_dev.Desafio_Livraria.dto.BookResponseDTO;
import caio_dev.Desafio_Livraria.dto.GenreReportDTO;
import caio_dev.Desafio_Livraria.entity.Book;
import caio_dev.Desafio_Livraria.entity.User;
import caio_dev.Desafio_Livraria.repository.BookRepository;
import caio_dev.Desafio_Livraria.repository.UserRepository;
@ExtendWith(MockitoExtension.class)
class BookServiceTest {
    @Mock   //Cria um versão simulada
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks //Injeta das depencias mocks num BookService mockado
    private BookService bookService;

    @Mock 
    private UserService userService;

    private UUID userId;
    private User currentUser;

    @BeforeEach 
    void setUp() {
        userId = UUID.randomUUID();
        currentUser = new User();
        currentUser.setId(userId);
        currentUser.setUsername("testuser"); 
        currentUser.setPassword("encodedPassword"); 
        currentUser.setRole("USER");
    }

    private Book createBook(UUID id, String title, String author, String genre, Integer publicationYear, UUID createdBy, UUID updatedBy, boolean deleted) {
        Book book = new Book();
        book.setId(id);
        book.setTitle(title);
        book.setAuthor(author);
        book.setGenre(genre);
        book.setPublicationYear(publicationYear);
        book.setCreatedAt(LocalDateTime.now().minusDays(5));
        book.setUpdatedAt(LocalDateTime.now().minusDays(1));
        book.setCreatedBy(createdBy);
        book.setUpdatedBy(updatedBy);
        book.setDeleted(deleted);
        return book;
    }

    private BookRequestDTO createBookRequestDTO(String title, String author, Integer publicationYear, String genre) {
        return new BookRequestDTO(title, author, publicationYear, genre);
    }

    // Helper para mockar SecurityContextHolder e UserRepository. Simulando o user logado.
    private void mockSecurityContextHolder() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        UserDetails userDetails = mock(UserDetails.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("testuser");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(currentUser));

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("Should create a book successfully")
    void createBook_Success() {
        // Arrange
        mockSecurityContextHolder(); // Chama aqui para configurar o SecurityContext E o userRepository mock
        BookRequestDTO request = createBookRequestDTO("Title 1", "Author 1", 2020, "Fiction");

        Book savedBook = createBook(UUID.randomUUID(), "Title 1", "Author 1", "Fiction", 2020, userId, null, false);
        savedBook.setCreatedAt(LocalDateTime.now()); // Set creation time for response mapping

        when(bookRepository.save(any(Book.class))).thenReturn(savedBook);

        // Act
        BookResponseDTO response = bookService.createBook(request);

        // Assert
        assertNotNull(response);
        assertEquals(savedBook.getId(), response.getId());
        assertEquals(request.getTitle(), response.getTitle());
        assertEquals(request.getAuthor(), response.getAuthor());
        assertEquals(request.getGenre(), response.getGenre());
        assertEquals(request.getPublicationYear(), response.getPublicationYear());
        assertEquals(userId, response.getCreatedBy());
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    @DisplayName("Should throw RuntimeException if current user is not found when creating a book")
    void createBook_UserNotFound() {
        // Arrange
        // Este teste configura seu próprio contexto de segurança para o cenário específico de "usuário não encontrado"
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        UserDetails userDetails = mock(UserDetails.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("nonexistent_user");
        when(userRepository.findByUsername("nonexistent_user")).thenReturn(Optional.empty()); // Stub específico para este teste

        SecurityContextHolder.setContext(securityContext);

        BookRequestDTO request = createBookRequestDTO("Title 1", "Author 1", 2020, "Fiction");

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> bookService.createBook(request));
        assertEquals("User not found: nonexistent_user", exception.getMessage());
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    @DisplayName("Should create multiple books in batch successfully")
    void createBooksBatch_Success() {
        // Arrange
        mockSecurityContextHolder(); // Chama aqui para configurar o SecurityContext E o userRepository mock
        BookRequestDTO request1 = createBookRequestDTO("Title 1", "Author 1", 2020, "Fiction");
        BookRequestDTO request2 = createBookRequestDTO("Title 2", "Author 2", 2021, "Science");
        BatchBookRequestDTO batchRequest = new BatchBookRequestDTO(List.of(request1, request2));

        Book book1 = createBook(UUID.randomUUID(), "Title 1", "Author 1", "Fiction", 2020, userId, null, false);
        Book book2 = createBook(UUID.randomUUID(), "Title 2", "Author 2", "Science", 2021, userId, null, false);
        book1.setCreatedAt(LocalDateTime.now());
        book2.setCreatedAt(LocalDateTime.now());

        when(bookRepository.saveAll(anyList())).thenReturn(List.of(book1, book2));

        // Act
        List<BookResponseDTO> responses = bookService.createBooksBatch(batchRequest);

        // Assert
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals(book1.getId(), responses.get(0).getId());
        assertEquals(book2.getId(), responses.get(1).getId());
        verify(bookRepository, times(1)).saveAll(anyList());
    }

    @Test
    @DisplayName("Should update an existing book successfully")
    void updateBook_Success() {
        // Arrange
        mockSecurityContextHolder(); // Chama aqui para configurar o SecurityContext E o userRepository mock
        UUID bookId = UUID.randomUUID();
        BookRequestDTO request = createBookRequestDTO("Updated Title", "Updated Author", 2022, "Fantasy");
        Book existingBook = createBook(bookId, "Old Title", "Old Author", "Fiction", 2020, UUID.randomUUID(), null, false);
        Book updatedBook = createBook(bookId, "Updated Title", "Updated Author", "Fantasy", 2022, existingBook.getCreatedBy(), userId, false);
        updatedBook.setCreatedAt(existingBook.getCreatedAt());
        updatedBook.setUpdatedAt(LocalDateTime.now());

        when(bookRepository.findOne(any(Specification.class))).thenReturn(Optional.of(existingBook));
        when(bookRepository.save(any(Book.class))).thenReturn(updatedBook);

        // Act
        BookResponseDTO response = bookService.updateBook(bookId, request);

        // Assert
        assertNotNull(response);
        assertEquals(bookId, response.getId());
        assertEquals(request.getTitle(), response.getTitle());
        assertEquals(request.getAuthor(), response.getAuthor());
        assertEquals(request.getGenre(), response.getGenre());
        assertEquals(request.getPublicationYear(), response.getPublicationYear());
        assertEquals(userId, response.getUpdatedBy());
        verify(bookRepository, times(1)).findOne(any(Specification.class));
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    @DisplayName("Should throw RuntimeException if book to update is not found")
    void updateBook_NotFound() {
        // Arrange
        UUID bookId = UUID.randomUUID();
        BookRequestDTO request = createBookRequestDTO("Updated Title", "Updated Author", 2022, "Fantasy");

        when(bookRepository.findOne(any(Specification.class))).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> bookService.updateBook(bookId, request));
        assertEquals("Book not found", exception.getMessage());
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    @DisplayName("Should mark a book as deleted successfully")
    void deleteBook_Success() {
        // Arrange
        mockSecurityContextHolder(); // Chama aqui para configurar o SecurityContext E o userRepository mock
        UUID bookId = UUID.randomUUID();
        Book existingBook = createBook(bookId, "Title", "Author", "Fiction", 2020, UUID.randomUUID(), null, false);
        Book deletedBook = createBook(bookId, "Title", "Author", "Fiction", 2020, existingBook.getCreatedBy(), userId, true);
        deletedBook.setCreatedAt(existingBook.getCreatedAt());
        deletedBook.setUpdatedAt(LocalDateTime.now()); // Simulate update time

        when(bookRepository.findOne(any(Specification.class))).thenReturn(Optional.of(existingBook));
        when(bookRepository.save(any(Book.class))).thenReturn(deletedBook);

        // Act
        bookService.deleteBook(bookId);

        // Assert
        verify(bookRepository, times(1)).findOne(any(Specification.class));
        verify(bookRepository, times(1)).save(argThat(book -> book.isDeleted() && book.getUpdatedBy().equals(userId)));
    }

    @Test
    @DisplayName("Should throw RuntimeException if book to delete is not found")
    void deleteBook_NotFound() {
        // Arrange
        UUID bookId = UUID.randomUUID();
        when(bookRepository.findOne(any(Specification.class))).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> bookService.deleteBook(bookId));
        assertEquals("Book not found", exception.getMessage());
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    @DisplayName("Should return all non-deleted books with pagination")
    void getAllBooks_Success() {
        // Arrange
        // Este teste não chama getCurrentUserId(), então NÃO precisa de mockSecurityContextHolder()
        Pageable pageable = PageRequest.of(0, 10);
        List<Book> books = List.of(
                createBook(UUID.randomUUID(), "Book A", "Author X", "Genre 1", 2000, UUID.randomUUID(), null, false),
                createBook(UUID.randomUUID(), "Book B", "Author Y", "Genre 2", 2005, UUID.randomUUID(), null, false)
        );
        Page<Book> bookPage = new PageImpl<>(books, pageable, books.size());

        when(bookRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(bookPage);

        // Act
        Page<BookResponseDTO> responsePage = bookService.getAllBooks(pageable);

        // Assert
        assertNotNull(responsePage);
        assertEquals(2, responsePage.getTotalElements());
        assertEquals("Book A", responsePage.getContent().get(0).getTitle());
        assertEquals("Book B", responsePage.getContent().get(1).getTitle());
        verify(bookRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @DisplayName("Should return empty page if no non-deleted books exist")
    void getAllBooks_Empty() {
        // Arrange
        // Este teste não chama getCurrentUserId(), então NÃO precisa de mockSecurityContextHolder()
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(bookRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(emptyPage);

        // Act
        Page<BookResponseDTO> responsePage = bookService.getAllBooks(pageable);

        // Assert
        assertNotNull(responsePage);
        assertTrue(responsePage.isEmpty());
        assertEquals(0, responsePage.getTotalElements());
        verify(bookRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @DisplayName("Should search books by title, author, genre, and publication year with pagination")
    void searchBooks_Success() {
        // Arrange
        // Este teste não chama getCurrentUserId(), então NÃO precisa de mockSecurityContextHolder()
        String title = "Java";
        String author = "Bloch";
        String genre = "Programming";
        Integer publicationYear = 2008;
        Pageable pageable = PageRequest.of(0, 10);

        List<Book> foundBooks = List.of(
                createBook(UUID.randomUUID(), "Effective Java", "Joshua Bloch", "Programming", 2008, UUID.randomUUID(), null, false)
        );
        Page<Book> foundBookPage = new PageImpl<>(foundBooks, pageable, foundBooks.size());

        when(bookRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(foundBookPage);

        // Act
        Page<BookResponseDTO> responsePage = bookService.searchBooks(title, author, genre, publicationYear, pageable);

        // Assert
        assertNotNull(responsePage);
        assertEquals(1, responsePage.getTotalElements());
        assertEquals("Effective Java", responsePage.getContent().get(0).getTitle());
        verify(bookRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @DisplayName("Should return empty page when no books match search criteria")
    void searchBooks_NoMatch() {
        // Arrange
        // Este teste não chama getCurrentUserId(), então NÃO precisa de mockSecurityContextHolder()
        String title = "NonExistent";
        String author = "Nobody";
        String genre = "Unknown";
        Integer publicationYear = 1900;
        Pageable pageable = PageRequest.of(0, 10);

        Page<Book> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(bookRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(emptyPage);

        // Act
        Page<BookResponseDTO> responsePage = bookService.searchBooks(title, author, genre, publicationYear, pageable);

        // Assert
        assertNotNull(responsePage);
        assertTrue(responsePage.isEmpty());
        verify(bookRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @DisplayName("Should generate genre report with correct counts and sorted by genre")
    void getBooksByGenreReport_Success() {
        // Arrange
        // Este teste não chama getCurrentUserId(), então NÃO precisa de mockSecurityContextHolder()
        List<Book> books = List.of(
                createBook(UUID.randomUUID(), "Book 1", "A1", "Fiction", 2000, UUID.randomUUID(), null, false),
                createBook(UUID.randomUUID(), "Book 2", "A2", "Science", 2001, UUID.randomUUID(), null, false),
                createBook(UUID.randomUUID(), "Book 3", "A3", "Fiction", 2002, UUID.randomUUID(), null, false),
                createBook(UUID.randomUUID(), "Book 4", "A4", "History", 2003, UUID.randomUUID(), null, false),
                createBook(UUID.randomUUID(), "Book 5", "A5", null, 2004, UUID.randomUUID(), null, false), // Book with null genre
                createBook(UUID.randomUUID(), "Book 6", "A6", "Science", 2005, UUID.randomUUID(), null, true) // Science 2, mas DELETED = true
        );
        // Assumindo que o serviço filtra livros deletados para o relatório.
        when(bookRepository.findAll(any(Specification.class))).thenReturn(books.stream().filter(book -> !book.isDeleted()).toList());

        // Act
        List<GenreReportDTO> report = bookService.getBooksByGenreReport();

        // Assert
        assertNotNull(report);
        assertEquals(4, report.size()); // Fiction, History, Science, Unknown

        // Verify sorted order and counts
        assertEquals("Fiction", report.get(0).getGenre());
        assertEquals(2L, report.get(0).getCount());

        assertEquals("History", report.get(1).getGenre());
        assertEquals(1L, report.get(1).getCount());

        assertEquals("Science", report.get(2).getGenre());
        assertEquals(1L, report.get(2).getCount()); // CORRIGIDO: Agora espera 1, pois o outro livro Science está deletado

        assertEquals("Unknown", report.get(3).getGenre());
        assertEquals(1L, report.get(3).getCount());

        verify(bookRepository, times(1)).findAll(any(Specification.class));
    }

    @Test
    @DisplayName("Should return empty genre report if no non-deleted books exist")
    void getBooksByGenreReport_Empty() {
        // Arrange
        // Este teste não chama getCurrentUserId(), então NÃO precisa de mockSecurityContextHolder()
        when(bookRepository.findAll(any(Specification.class))).thenReturn(Collections.emptyList());

        // Act
        List<GenreReportDTO> report = bookService.getBooksByGenreReport();

        // Assert
        assertNotNull(report);
        assertTrue(report.isEmpty());
        verify(bookRepository, times(1)).findAll(any(Specification.class));
    }
}