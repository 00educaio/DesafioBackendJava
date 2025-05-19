package caio_dev.Desafio_Livraria.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BookRequestDTO {
    @NotBlank(message = "Title is mandatory")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    @Size(max = 255, message = "Author must not exceed 255 characters")
    private String author;

    @Min(value = 1, message = "Publication year must be greater than 0")
    private Integer publicationYear;

    @Size(max = 100, message = "Genre must not exceed 100 characters")
    private String genre;
}