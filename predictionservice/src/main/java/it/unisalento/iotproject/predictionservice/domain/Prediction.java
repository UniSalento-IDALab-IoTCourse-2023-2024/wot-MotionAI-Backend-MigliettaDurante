package it.unisalento.iotproject.predictionservice.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "predictions")
public class Prediction {

    @Id
    private String id;
    private String userEmail;
    private String deviceId;
    private String prediction;
    private Long duration;
    private String date;

    public Prediction() {
    }

    public Prediction(String id, String userEmail, String deviceId, String prediction, Long duration, String date) {
        this.id = id;
        this.userEmail = userEmail;
        this.deviceId = deviceId;
        this.prediction = prediction;
        this.duration = duration;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getPrediction() {
        return prediction;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public void setPrediction(String prediction) {
        this.prediction = prediction;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
