package it.unisalento.recproject.authenticationservice.RestController;

import it.unisalento.recproject.authenticationservice.domain.User;
import it.unisalento.recproject.authenticationservice.dto.AuthenticationResponseDTO;
import it.unisalento.recproject.authenticationservice.dto.LoginDTO;
import it.unisalento.recproject.authenticationservice.repositories.UserRepository;
import it.unisalento.recproject.authenticationservice.security.JwtUtilities;
import it.unisalento.recproject.authenticationservice.service.CustomDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/login")
public class LoginController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtilities jwtUtilities;

    @Autowired
    private CustomDetailsService customDetailsService;

    @RequestMapping(value="/", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationTokenUser(@RequestBody(required = false) LoginDTO loginDTO) {

        if (loginDTO == null) {
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body("Request body is null");
        }

        try {
            if (loginDTO.getEmail() == null || loginDTO.getPassword() == null) {
                return ResponseEntity.badRequest().body("Missing email or password");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Missing email or password");
        }

        Authentication authentication;

        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDTO.getEmail(),
                            loginDTO.getPassword()
                    )
            );
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Authentication failed");
        }

        User user = userRepository.findByEmail(authentication.getName());

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        final String jwt = jwtUtilities.generateToken(loginDTO.getEmail());

        return ResponseEntity.ok(new AuthenticationResponseDTO(jwt));
    }
}

