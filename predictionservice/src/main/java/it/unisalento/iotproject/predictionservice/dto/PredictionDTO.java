package it.unisalento.iotproject.predictionservice.dto;

public class PredictionDTO {

    private String id;
    private String userEmail;
    private String deviceId;
    private String prediction;
    private String date;

    public PredictionDTO() {
    }

    public PredictionDTO(String id, String userEmail, String deviceId, String prediction, String date) {
        this.id = id;
        this.userEmail = userEmail;
        this.deviceId = deviceId;
        this.prediction = prediction;
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
