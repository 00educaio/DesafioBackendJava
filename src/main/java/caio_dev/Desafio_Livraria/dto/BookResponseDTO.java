package caio_dev.Desafio_Livraria.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class BookResponseDTO {
    private UUID id;
    private String title;
    private String author;
    private Integer publicationYear;
    private String genre;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UUID createdBy;
    private UUID updatedBy;
}