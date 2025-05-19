package caio_dev.Desafio_Livraria.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record BatchBookRequestDTO(
        @NotEmpty @Valid List<BookRequestDTO> books) {
}