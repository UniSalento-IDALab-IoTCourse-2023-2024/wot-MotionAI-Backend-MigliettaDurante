package it.unisalento.iotproject.predictionservice.restcontrollers;

import it.unisalento.iotproject.predictionservice.domain.Prediction;
import it.unisalento.iotproject.predictionservice.dto.ActivityDurationDTO;
import it.unisalento.iotproject.predictionservice.dto.ListPredictionDTO;
import it.unisalento.iotproject.predictionservice.dto.PredictionDTO;
import it.unisalento.iotproject.predictionservice.repository.PredictionRepository;
import it.unisalento.iotproject.predictionservice.response.MessageResponse;
import it.unisalento.iotproject.predictionservice.security.AuthorizationAPI;
import it.unisalento.iotproject.predictionservice.security.JwtUtilities;
import it.unisalento.iotproject.predictionservice.service.PredictionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
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
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse("User not authorized"));
            }

            Prediction prediction = new Prediction();

            prediction.setUserEmail(userEmail);

            if (predictionDTO.getPrediction() == null || predictionDTO.getDeviceId() == null || predictionDTO.getDate() == null) {
                return ResponseEntity.badRequest().body(new MessageResponse("Missing prediction or deviceId or date"));
            }

            prediction.setDate(predictionDTO.getDate());
            prediction.setPrediction(predictionDTO.getPrediction());
            prediction.setDuration(predictionDTO.getDuration());
            prediction.setDeviceId(predictionDTO.getDeviceId());

            prediction = predictionRepository.save(prediction);

            predictionDTO.setId(prediction.getId());

            return ResponseEntity.ok(new MessageResponse("Prediction inserted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error inserting prediction"));
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
                        LocalDate date1 = LocalDate.parse(p1.getDate(), DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                        LocalDate date2 = LocalDate.parse(p2.getDate(), DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                        return date2.compareTo(date1);
                    })
                    .toList();

            for (Prediction prediction : predictions) {
                PredictionDTO predictionDTO = new PredictionDTO();

                predictionDTO.setId(prediction.getId());
                predictionDTO.setUserEmail(prediction.getUserEmail());
                predictionDTO.setDeviceId(prediction.getDeviceId());
                predictionDTO.setPrediction(prediction.getPrediction());
                predictionDTO.setDuration(prediction.getDuration());
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

            return ResponseEntity.ok(new MessageResponse("Predictions deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Predictions deleted successfully"));
        }
    }

    @RequestMapping(value = "/getWeeklyActivityComparison", method = RequestMethod.GET)
    public ResponseEntity<?> getWeeklyActivityComparison(HttpServletRequest request) {
        return predictionService.getWeeklyActivityComparison(request);
    }

    @RequestMapping(value = "/getTodayActivityDurations", method = RequestMethod.GET)
    public ResponseEntity<?> getTodayActivityDurations(@RequestParam(value = "date", required = false) String date, HttpServletRequest request) {
        try {
            return predictionService.getActivityDurationsForToday(request, date);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error getting today's activity durations");
        }
    }

    @RequestMapping(value = "/getActivityByUser", method = RequestMethod.GET)
    public ResponseEntity<?> getActivityDurationHistory(HttpServletRequest request) {
        AuthorizationAPI authorizationAPI = new AuthorizationAPI();
        authorizationAPI.checkAuthorizationRole(request, jwtUtilities);

        try {

            String userEmail = jwtUtilities.extractUsername(request.getHeader("Authorization").substring(7));

            List<ActivityDurationDTO> activity = predictionRepository.aggregateDurationsByDayAndUser(userEmail);

            return ResponseEntity.ok(activity);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error getting activity duration history");
        }
    }
}
