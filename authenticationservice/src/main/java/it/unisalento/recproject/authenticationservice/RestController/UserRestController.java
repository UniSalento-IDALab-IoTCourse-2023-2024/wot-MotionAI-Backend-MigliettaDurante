package it.unisalento.recproject.authenticationservice.RestController;

import it.unisalento.recproject.authenticationservice.domain.User;
import it.unisalento.recproject.authenticationservice.dto.UserDTO;
import it.unisalento.recproject.authenticationservice.repositories.UserRepository;
import it.unisalento.recproject.authenticationservice.security.JwtUtilities;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

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
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Token non valido");

        User user = userRepository.findByEmail(email);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        userRepository.deleteByEmail(email);

        return ResponseEntity.ok("L'utente con email " + email + " è stato eliminato");
    }

    @RequestMapping(value = "/", method = RequestMethod.PUT)
    public ResponseEntity<?> put(@RequestBody(required = false) UserDTO userDTO, HttpServletRequest request) {

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

        if (userDTO == null) {
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body("Request body is null");
        }

        user.setNome(userDTO.getNome());
        user.setCognome(userDTO.getCognome());
        user.setDataNascita(userDTO.getDataNascita());
        user.setPassword(userDTO.getPassword());

        userRepository.save(user);

        userDTO.setId(user.getId());
        return ResponseEntity.ok(userDTO);
    }
}
