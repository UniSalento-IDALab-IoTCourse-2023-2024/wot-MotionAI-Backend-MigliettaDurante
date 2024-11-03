package it.unisalento.recproject.authenticationservice.service;

import it.unisalento.recproject.authenticationservice.domain.User;
import it.unisalento.recproject.authenticationservice.dto.UserDTO;
import it.unisalento.recproject.authenticationservice.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static it.unisalento.recproject.authenticationservice.configuration.SecurityConfig.passwordEncoder;

@Service
public class CustomDetailsService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String emailOrUsername) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(emailOrUsername);
        UserDetails userDetails;

        if (user != null) {
            userDetails = org.springframework.security.core.userdetails.User
                    .withUsername(user.getEmail())
                    .password(user.getPassword())
                    .roles("USER")
                    .build();
            return userDetails;
        }
        else return null;
    }

    public ResponseEntity<?> registrazioneUser(UserDTO userDTO){
        if (!userRepository.existsByEmail(userDTO.getEmail()) ) {
            User user = new User();
            user.setNome(userDTO.getNome());
            user.setCognome(userDTO.getCognome());
            user.setDataNascita(userDTO.getDataNascita());
            user.setEmail(userDTO.getEmail());
            user.setPassword(passwordEncoder().encode(userDTO.getPassword()));

            user = userRepository.save(user);

            userDTO.setId(user.getId());
            userDTO.setPassword(null);

            return ResponseEntity.ok(userDTO);
        }
        else return new ResponseEntity<>("La registrazione non è andata a buon fine!", HttpStatus.BAD_REQUEST);
    }
}
