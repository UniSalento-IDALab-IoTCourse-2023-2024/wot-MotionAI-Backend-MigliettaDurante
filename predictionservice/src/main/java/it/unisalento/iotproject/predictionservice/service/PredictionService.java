package it.unisalento.iotproject.predictionservice.service;

import it.unisalento.iotproject.predictionservice.domain.Prediction;
import it.unisalento.iotproject.predictionservice.repository.PredictionRepository;
import it.unisalento.iotproject.predictionservice.security.AuthorizationAPI;
import it.unisalento.iotproject.predictionservice.security.JwtUtilities;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PredictionService {

    @Autowired
    private PredictionRepository predictionRepository;

    @Autowired
    private JwtUtilities jwtUtilities;

    public ResponseEntity<?> getWeeklyActivityComparison(HttpServletRequest request) {

        AuthorizationAPI authorizationAPI = new AuthorizationAPI();
        authorizationAPI.checkAuthorizationRole(request, jwtUtilities);

        try {
            String userEmail = jwtUtilities.extractUsername(request.getHeader("Authorization").substring(7));

            LocalDate today = LocalDate.now();
            LocalDate currentWeekStart = today.with(ChronoField.DAY_OF_WEEK, 1);
            LocalDate currentWeekEnd = currentWeekStart.plusDays(6);
            LocalDate previousWeekStart = currentWeekStart.minusWeeks(1);
            LocalDate previousWeekEnd = currentWeekEnd.minusWeeks(1);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

            List<Prediction> recentPredictions = predictionRepository.findByUserEmail(userEmail).stream()
                    .filter(prediction -> {
                        try {
                            LocalDate predictionDate = LocalDate.parse(prediction.getDate(), formatter);
                            return !predictionDate.isBefore(currentWeekStart) &&
                                    !predictionDate.isAfter(currentWeekEnd);
                        } catch (Exception e) {
                            return false;
                        }
                    })
                    .toList();

            if (recentPredictions.isEmpty()) {
                return ResponseEntity.ok("Nessuna attività registrata nella settimana corrente.");
            }

            Map<String, Long> recentActivityDurations = recentPredictions.stream()
                    .collect(Collectors.groupingBy(Prediction::getPrediction,
                            Collectors.summingLong(Prediction::getDuration)));

            List<Prediction> previousWeekPredictions = predictionRepository.findByUserEmail(userEmail).stream()
                    .filter(prediction -> {
                        try {
                            // Usa LocalDate per analizzare solo la parte della data
                            LocalDate predictionDate = LocalDate.parse(prediction.getDate(), formatter);
                            return !predictionDate.isBefore(previousWeekStart) &&
                                    !predictionDate.isAfter(previousWeekEnd);
                        } catch (Exception e) {
                            return false;
                        }
                    })
                    .toList();

            if (previousWeekPredictions.isEmpty()) {
                return ResponseEntity.ok("Nessun dato disponibile per il confronto con la settimana precedente.");
            }

            Map<String, Long> previousActivityDurations = previousWeekPredictions.stream()
                    .collect(Collectors.groupingBy(Prediction::getPrediction,
                            Collectors.summingLong(Prediction::getDuration)));


            Map<String, String> activityComparison = new HashMap<>();
            List<String> activities = List.of("Stationary", "Running", "Walking", "Driving");
            for (String activity : activities) {
                long recentDuration = recentActivityDurations.getOrDefault(activity, 0L);
                long previousDuration = previousActivityDurations.getOrDefault(activity, 0L);

                double change;
                if (previousDuration > 0) {
                    change = ((double)(recentDuration - previousDuration) / previousDuration) * 100;
                } else {
                    change = recentDuration > 0 ? 100.0 : 0.0;
                }

                String changeString = String.format("%+.2f%%", change);
                activityComparison.put(activity, changeString);
            }

            return ResponseEntity.ok(activityComparison);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Errore nel confronto settimanale delle attività.");
        }
    }

    public ResponseEntity<?> getActivityDurationsForToday(HttpServletRequest request,
                                                          @RequestParam(value = "date", required = false) String date) {

        AuthorizationAPI authorizationAPI = new AuthorizationAPI();
        authorizationAPI.checkAuthorizationRole(request, jwtUtilities);

        try {
            String userEmail = jwtUtilities.extractUsername(request.getHeader("Authorization").substring(7));

            String selectedDate = (date != null) ? date : LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

            List<Prediction> activities = predictionRepository.findByUserEmailAndDateStartingWith(userEmail, selectedDate);

            if (activities.isEmpty()) {
                return ResponseEntity.ok("No activities performed today.");
            }

            Map<String, Long> activityDurations = new HashMap<>();

            for (Prediction activity : activities) {

                activityDurations.merge(activity.getPrediction(), activity.getDuration(), Long::sum);
            }

            return ResponseEntity.ok(activityDurations);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error getting today's activity durations");
        }
    }
}
