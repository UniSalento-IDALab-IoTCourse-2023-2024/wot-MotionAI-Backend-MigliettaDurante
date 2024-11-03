package it.unisalento.iotproject.predictionservice.repository;

import it.unisalento.iotproject.predictionservice.domain.Prediction;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PredictionRepository extends MongoRepository<Prediction, String> {
    List<Prediction> findByUserEmail(String userEmail);

}
