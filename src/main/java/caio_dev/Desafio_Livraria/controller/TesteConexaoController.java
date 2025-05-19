package caio_dev.Desafio_Livraria.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TesteConexaoController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/teste-conexao")
    public String testarConexao() {
        try {
            jdbcTemplate.execute("SELECT 1");
            return "Conexão com o banco de dados bem-sucedida!";
        } catch (Exception e) {
            return "Erro na conexão: " + e.getMessage();
        }
    }
}
