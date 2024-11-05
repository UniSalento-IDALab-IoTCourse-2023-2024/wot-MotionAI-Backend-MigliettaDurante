package it.unisalento.iotproject.predictionservice.repository;

import it.unisalento.iotproject.predictionservice.domain.Prediction;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface PredictionRepository extends MongoRepository<Prediction, String> {
    List<Prediction> findByUserEmail(String userEmail);
    List<Prediction> findByUserEmailAndDateStartingWith(String userEmail, String datePrefix);
}
