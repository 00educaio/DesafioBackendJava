package caio_dev.Desafio_Livraria.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor // Gera um construtor com todos os campos
@NoArgsConstructor
public class GenreReportDTO {
    private String genre;
    private long count;
}