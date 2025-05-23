package caio_dev.Desafio_Livraria.service;

import caio_dev.Desafio_Livraria.entity.User;
import caio_dev.Desafio_Livraria.repository.UserRepository;
import caio_dev.Desafio_Livraria.security.MyUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public MyUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));
        return new MyUserDetails(user);
    }
}

// Uma implementação personalizada da interface UserDetailsService do Spring Security. É responsável por carregar os detalhes do usuário (como nome de usuário e senha) durante o processo de autenticação