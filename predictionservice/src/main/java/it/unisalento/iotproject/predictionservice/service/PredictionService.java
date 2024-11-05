package it.unisalento.iotproject.predictionservice.service;

import it.unisalento.iotproject.predictionservice.domain.Prediction;
import it.unisalento.iotproject.predictionservice.repository.PredictionRepository;
import it.unisalento.iotproject.predictionservice.security.AuthorizationAPI;
import it.unisalento.iotproject.predictionservice.security.JwtUtilities;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");


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

            Map<String, Object> activityComparison = new HashMap<>();
            for (String activity : activities) {
                double recentPercentage = recentActivityPercentages.getOrDefault(activity, 0.0);
                double previousPercentage = previousActivityPercentages.getOrDefault(activity, 0.0);
                double change = recentPercentage - previousPercentage;


                String changeString = String.format("%+.2f%%", change);
                activityComparison.put(activity, changeString);
            }

            return ResponseEntity.ok(activityComparison);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Errore nel confronto settimanale delle attività.");
        }
    }

    public ResponseEntity<?> getActivityDurationsForToday(HttpServletRequest request) {

        AuthorizationAPI authorizationAPI = new AuthorizationAPI();
        authorizationAPI.checkAuthorizationRole(request, jwtUtilities);

        try {
            String userEmail = jwtUtilities.extractUsername(request.getHeader("Authorization").substring(7));

            String today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

            List<Prediction> activities = predictionRepository.findByUserEmailAndDateStartingWith(userEmail, today);

            if (activities.isEmpty()) {
                return ResponseEntity.ok("No activities performed today.");
            }

            activities.sort(Comparator.comparing(a -> LocalDateTime.parse(a.getDate(), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))));

            Map<String, Long> activityDurations = new HashMap<>();
            for (int i = 0; i < activities.size() - 1; i++) {
                Prediction current = activities.get(i);
                Prediction next = activities.get(i + 1);

                LocalDateTime currentTime = LocalDateTime.parse(current.getDate(), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
                LocalDateTime nextTime = LocalDateTime.parse(next.getDate(), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));

                long duration = Duration.between(currentTime, nextTime).getSeconds();
                activityDurations.merge(current.getPrediction(), duration, Long::sum);
            }

            return ResponseEntity.ok(activityDurations);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error getting today's activity durations");
        }
    }
}
