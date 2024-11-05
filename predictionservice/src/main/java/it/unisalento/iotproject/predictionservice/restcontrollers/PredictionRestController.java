package it.unisalento.iotproject.predictionservice.restcontrollers;


import it.unisalento.iotproject.predictionservice.domain.Prediction;
import it.unisalento.iotproject.predictionservice.dto.ListPredictionDTO;
import it.unisalento.iotproject.predictionservice.dto.PredictionDTO;
import it.unisalento.iotproject.predictionservice.repository.PredictionRepository;
import it.unisalento.iotproject.predictionservice.security.AuthorizationAPI;
import it.unisalento.iotproject.predictionservice.security.JwtUtilities;
import it.unisalento.iotproject.predictionservice.service.PredictionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/prediction")
public class PredictionRestController {

    @Autowired
    PredictionRepository predictionRepository;

    @Autowired
    private JwtUtilities jwtUtilities;

    @Autowired
    private PredictionService predictionService;

    @RequestMapping(value = "/insert", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> insertPrediction(@RequestBody PredictionDTO predictionDTO, HttpServletRequest request) {

        AuthorizationAPI authorizationAPI = new AuthorizationAPI();
        authorizationAPI.checkAuthorizationRole(request, jwtUtilities);

        try {

            String userEmail = jwtUtilities.extractUsername(request.getHeader("Authorization").substring(7));

            if (predictionDTO.getUserEmail() != null && !predictionDTO.getUserEmail().equals(userEmail)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User not authorized");
            }

            Prediction prediction = new Prediction();

            prediction.setUserEmail(userEmail);
            prediction.setDate(predictionDTO.getDate());

            if (predictionDTO.getPrediction() == null || predictionDTO.getDeviceId() == null || predictionDTO.getDate() == null) {
                return ResponseEntity.badRequest().body("Missing prediction or deviceId or date");
            }

            prediction.setPrediction(predictionDTO.getPrediction());
            prediction.setDeviceId(predictionDTO.getDeviceId());

            prediction = predictionRepository.save(prediction);

            predictionDTO.setId(prediction.getId());

            return ResponseEntity.ok("Prediction inserted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error inserting prediction");
        }
    }

    @RequestMapping(value = "/getByEmail", method = RequestMethod.GET)
    public ResponseEntity<?> getPredictions(HttpServletRequest request) {

        AuthorizationAPI authorizationAPI = new AuthorizationAPI();
        authorizationAPI.checkAuthorizationRole(request, jwtUtilities);

        try {

            ArrayList<PredictionDTO> list = new ArrayList<>();
            ListPredictionDTO listPredictionDTO = new ListPredictionDTO();

            String email = jwtUtilities.extractUsername(request.getHeader("Authorization").substring(7));

            List<Prediction> predictions = predictionRepository.findByUserEmail(email).stream()
                    .sorted((p1, p2) -> {
                        LocalDateTime date1 = LocalDateTime.parse(p1.getDate(), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
                        LocalDateTime date2 = LocalDateTime.parse(p2.getDate(), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
                        return date2.compareTo(date1);
                    })
                    .toList();

            for (Prediction prediction : predictions) {
                PredictionDTO predictionDTO = new PredictionDTO();
                predictionDTO.setId(prediction.getId());
                predictionDTO.setUserEmail(prediction.getUserEmail());
                predictionDTO.setDeviceId(prediction.getDeviceId());
                predictionDTO.setPrediction(prediction.getPrediction());
                predictionDTO.setDate(prediction.getDate());
                list.add(predictionDTO);
            }

            listPredictionDTO.setList(list);

            return ResponseEntity.ok(listPredictionDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error getting predictions");
        }
    }

    @RequestMapping(value = "/", method = RequestMethod.DELETE)
    public ResponseEntity<?> deletePredictionsByUser(HttpServletRequest request) {

        AuthorizationAPI authorizationAPI = new AuthorizationAPI();
        authorizationAPI.checkAuthorizationRole(request, jwtUtilities);

        try {

            String email = jwtUtilities.extractUsername(request.getHeader("Authorization").substring(7));

            List<Prediction> predictions = predictionRepository.findByUserEmail(email);

            predictionRepository.deleteAll(predictions);

            return ResponseEntity.ok("Predictions deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting predictions");
        }
    }

    @RequestMapping(value = "/getWeeklyActivityComparison", method = RequestMethod.GET)
    public ResponseEntity<?> getWeeklyActivityComparison(HttpServletRequest request) {
        return predictionService.getWeeklyActivityComparison(request);
    }

    @RequestMapping(value = "/getTodayActivityDurations", method = RequestMethod.GET)
    public ResponseEntity<?> getTodayActivityDurations(HttpServletRequest request) {
        try {
            return predictionService.getActivityDurationsForToday(request);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error getting today's activity durations");
        }
    }
}
