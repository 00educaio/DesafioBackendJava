package caio_dev.Desafio_Livraria.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = false)
public record BatchBookRequestDTO(
        @NotEmpty(message = "Books list cannot be empty")
        @Valid
        List<BookRequestDTO> books) {
}