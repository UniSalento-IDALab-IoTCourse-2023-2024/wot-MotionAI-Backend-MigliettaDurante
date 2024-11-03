package it.unisalento.recproject.authenticationservice.RestController;

import it.unisalento.recproject.authenticationservice.dto.UserDTO;
import it.unisalento.recproject.authenticationservice.repositories.UserRepository;
import it.unisalento.recproject.authenticationservice.service.CustomDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/registrazione")
public class RegistrationController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    CustomDetailsService customService;

    @RequestMapping(value = "/user", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registrazione(@RequestBody(required = false) UserDTO userDTO){
        if (userDTO == null) {
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body("Request body is null");
        }
        return customService.registrazioneUser(userDTO);
    }
}



