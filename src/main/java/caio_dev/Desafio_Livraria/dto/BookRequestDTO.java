package caio_dev.Desafio_Livraria.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

@AllArgsConstructor // Gera um construtor com todos os campos
@NoArgsConstructor  // Gera um construtor sem argumentos
@JsonIgnoreProperties(ignoreUnknown = false)
public class BookRequestDTO {
    @NotBlank(message = "Title is mandatory")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    @NotBlank(message = "Author is mandatory")
    @Size(max = 255, message = "Author must not exceed 255 characters")
    private String author;

    @Min(value = 1, message = "Publication year must be greater than 0")
    private Integer publicationYear;

    @Size(max = 100, message = "Genre must not exceed 100 characters")
    private String genre;
}