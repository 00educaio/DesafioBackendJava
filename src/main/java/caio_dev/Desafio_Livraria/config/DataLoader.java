package caio_dev.Desafio_Livraria.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import caio_dev.Desafio_Livraria.service.UserService;

@Component
public class DataLoader implements CommandLineRunner {

    private final UserService userService;

    public DataLoader(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(String... args) throws Exception {
        userService.createUser("david", "calvo", "USER");
    }
}
