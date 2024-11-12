package it.unisalento.recproject.authenticationservice.RestController;

import it.unisalento.recproject.authenticationservice.domain.User;
import it.unisalento.recproject.authenticationservice.dto.UserDTO;
import it.unisalento.recproject.authenticationservice.dto.UsersListDTO;
import it.unisalento.recproject.authenticationservice.repositories.UserRepository;
import it.unisalento.recproject.authenticationservice.response.MessageResponse;
import it.unisalento.recproject.authenticationservice.security.JwtUtilities;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

import static it.unisalento.recproject.authenticationservice.configuration.SecurityConfig.passwordEncoder;


@RestController
@RequestMapping("/api/user")
public class UserRestController {

    @Autowired
    JwtUtilities jwtUtilities;
    @Autowired
    UserRepository userRepository;

    @RequestMapping(value = "/", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteUser(HttpServletRequest request) {

        String authorizationHeader = request.getHeader("Authorization");

        String email;
        String jwt;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            email = jwtUtilities.extractUsername(jwt);
        }
        else
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse("Token non valido"));

        User user = userRepository.findByEmail(email);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("User not found"));
        }

        userRepository.deleteByEmail(email);

        return ResponseEntity.ok(new MessageResponse("User deleted"));
    }

    @RequestMapping(value = "/", method = RequestMethod.PUT)
    public ResponseEntity<?> put(@RequestBody(required = false) UserDTO userDTO, HttpServletRequest request) {

        String authorizationHeader = request.getHeader("Authorization");

        String email;
        String jwt;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            email = jwtUtilities.extractUsername(jwt);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Token non valido");
        }

        User user = userRepository.findByEmail(email);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        if (userDTO == null) {
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body("Request body is null");
        }

        if (userDTO.getNome() != null && !userDTO.getNome().isEmpty()) {
            user.setNome(userDTO.getNome());
        }
        if (userDTO.getCognome() != null && !userDTO.getCognome().isEmpty()) {
            user.setCognome(userDTO.getCognome());
        }
        if (userDTO.getDataNascita() != null) {
            user.setDataNascita(userDTO.getDataNascita());
        }
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder().encode(userDTO.getPassword()));
        }

        userRepository.save(user);

        userDTO.setId(user.getId());
        return ResponseEntity.ok(user);
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity<?> get(HttpServletRequest request) {

        String authorizationHeader = request.getHeader("Authorization");

        String email;
        String jwt;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            email = jwtUtilities.extractUsername(jwt);
        }
        else
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Token non valido");

        User user = userRepository.findByEmail(email);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        UserDTO userDTO = new UserDTO();

        userDTO.setId(user.getId());
        userDTO.setNome(user.getNome());
        userDTO.setCognome(user.getCognome());
        userDTO.setDataNascita(user.getDataNascita());
        userDTO.setEmail(user.getEmail());

        return ResponseEntity.ok(userDTO);
    }

    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    public UsersListDTO getAll(){

        ArrayList<UserDTO> list = new ArrayList<>();
        UsersListDTO usersList = new UsersListDTO();
        usersList.setList(list);

        List<User> users = userRepository.findAll();

        for (User user : users){
            UserDTO userDTO = new UserDTO();

            userDTO.setId(user.getId());
            userDTO.setNome(user.getNome());
            userDTO.setCognome(user.getCognome());
            userDTO.setDataNascita(user.getDataNascita());
            userDTO.setEmail(user.getEmail());
            userDTO.setPassword(user.getPassword());

            list.add(userDTO);
        }

        return usersList;
    }
}
