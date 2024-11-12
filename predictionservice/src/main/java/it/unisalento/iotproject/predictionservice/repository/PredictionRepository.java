package it.unisalento.iotproject.predictionservice.repository;

import it.unisalento.iotproject.predictionservice.domain.Prediction;
import it.unisalento.iotproject.predictionservice.dto.ActivityDurationDTO;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface PredictionRepository extends MongoRepository<Prediction, String> {

    @Aggregation(pipeline = {
            "{ $match: { userEmail: ?0 } }",
            "{ $group: { " +
                    "_id: { " +
                    "date: '$date', " +
                    "prediction: '$prediction', " +
                    "userEmail: '$userEmail' " +
                    "}, " +
                    "totalDuration: { $sum: '$duration' } " +
                    "} }",
            "{ $group: { " +
                    "_id: { " +
                    "date: '$_id.date', " +
                    "userEmail: '$_id.userEmail' " +
                    "}, " +
                    "activities: { " +
                    "$push: { " +
                    "k: '$_id.prediction', " +
                    "v: { $toInt: '$totalDuration' } " +
                    "} " +
                    "} " +
                    "} }",
            "{ $project: { " +
                    "_id: 0, " +
                    "date: '$_id.date', " +
                    "userEmail: '$_id.userEmail', " +
                    "activities: { $arrayToObject: '$activities' } " +
                    "} }",
            "{ $sort: { date: -1 } }"
    })
    List<ActivityDurationDTO> aggregateDurationsByDayAndUser(String userEmail);

    List<Prediction> findByUserEmail(String userEmail);
    List<Prediction> findByUserEmailAndDateStartingWith(String userEmail, String datePrefix);
}
