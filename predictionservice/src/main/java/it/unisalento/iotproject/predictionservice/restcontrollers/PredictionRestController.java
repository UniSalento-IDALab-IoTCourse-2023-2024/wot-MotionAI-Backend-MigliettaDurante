package it.unisalento.iotproject.predictionservice.restcontrollers;


import it.unisalento.iotproject.predictionservice.domain.Prediction;
import it.unisalento.iotproject.predictionservice.dto.ListPredictionDTO;
import it.unisalento.iotproject.predictionservice.dto.PredictionDTO;
import it.unisalento.iotproject.predictionservice.repository.PredictionRepository;
import it.unisalento.iotproject.predictionservice.security.AuthorizationAPI;
import it.unisalento.iotproject.predictionservice.security.JwtUtilities;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/prediction")
public class PredictionRestController {

    @Autowired
    PredictionRepository predictionRepository;

    @Autowired
    private JwtUtilities jwtUtilities;

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
        AuthorizationAPI authorizationAPI = new AuthorizationAPI();
        authorizationAPI.checkAuthorizationRole(request, jwtUtilities);

        try {
            String userEmail = jwtUtilities.extractUsername(request.getHeader("Authorization").substring(7));

            // Calcola l'inizio e la fine della settimana corrente e della settimana precedente
            LocalDate today = LocalDate.now();
            LocalDate currentWeekStart = today.with(ChronoField.DAY_OF_WEEK, 1);
            LocalDate currentWeekEnd = currentWeekStart.plusDays(6);
            LocalDate previousWeekStart = currentWeekStart.minusWeeks(1);
            LocalDate previousWeekEnd = currentWeekEnd.minusWeeks(1);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

            // Predizioni della settimana corrente
            List<Prediction> recentPredictions = predictionRepository.findByUserEmail(userEmail).stream()
                    .filter(prediction -> {
                        try {
                            LocalDateTime predictionDate = LocalDateTime.parse(prediction.getDate(), formatter);
                            return !predictionDate.toLocalDate().isBefore(currentWeekStart) &&
                                    !predictionDate.toLocalDate().isAfter(currentWeekEnd);
                        } catch (Exception e) {
                            return false;
                        }
                    })
                    .toList();

            if (recentPredictions.isEmpty()) {
                return ResponseEntity.ok("Nessuna attività registrata nella settimana corrente.");
            }

            Map<String, Long> recentActivityCounts = recentPredictions.stream()
                    .collect(Collectors.groupingBy(Prediction::getPrediction, Collectors.counting()));
            long totalRecentPredictions = recentPredictions.size();

            Map<String, Double> recentActivityPercentages = new HashMap<>();
            List<String> activities = List.of("Stationary", "Running", "Walking", "Driving");
            for (String activity : activities) {
                long count = recentActivityCounts.getOrDefault(activity, 0L);
                double percentage = (count * 100.0) / totalRecentPredictions;
                recentActivityPercentages.put(activity, percentage);
            }

            // Predizioni della settimana precedente
            List<Prediction> previousWeekPredictions = predictionRepository.findByUserEmail(userEmail).stream()
                    .filter(prediction -> {
                        try {
                            LocalDateTime predictionDate = LocalDateTime.parse(prediction.getDate(), formatter);
                            return !predictionDate.toLocalDate().isBefore(previousWeekStart) &&
                                    !predictionDate.toLocalDate().isAfter(previousWeekEnd);
                        } catch (Exception e) {
                            return false;
                        }
                    })
                    .toList();

            if (previousWeekPredictions.isEmpty()) {
                return ResponseEntity.ok("Nessun dato disponibile per il confronto con la settimana precedente.");
            }

            Map<String, Long> previousActivityCounts = previousWeekPredictions.stream()
                    .collect(Collectors.groupingBy(Prediction::getPrediction, Collectors.counting()));
            long totalPreviousPredictions = previousWeekPredictions.size();

            Map<String, Double> previousActivityPercentages = new HashMap<>();
            for (String activity : activities) {
                long count = previousActivityCounts.getOrDefault(activity, 0L);
                double percentage = (count * 100.0) / totalPreviousPredictions;
                previousActivityPercentages.put(activity, percentage);
            }

            // Calcolo delle variazioni percentuali
            Map<String, Object> activityComparison = new HashMap<>();
            for (String activity : activities) {
                double recentPercentage = recentActivityPercentages.getOrDefault(activity, 0.0);
                double previousPercentage = previousActivityPercentages.getOrDefault(activity, 0.0);
                double change = recentPercentage - previousPercentage;

                // Formatta la stringa con segno + e due decimali
                String changeString = String.format("%+.2f%%", change);
                activityComparison.put(activity, changeString);
            }

            return ResponseEntity.ok(activityComparison);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Errore nel confronto settimanale delle attività.");
        }
    }
}
