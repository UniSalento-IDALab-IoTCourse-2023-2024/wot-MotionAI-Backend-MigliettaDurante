package it.unisalento.iotproject.predictionservice.dto;

import java.util.Map;

public class ActivityDurationDTO {
    private String userEmail;
    private Map<String, Long> activities;
    private String date;

    public ActivityDurationDTO() {
    }

    public ActivityDurationDTO(String userEmail, Map<String, Long> activity, String date) {
        this.userEmail = userEmail;
        this.activities = activity;
        this.date = date;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Map<String, Long> getActivity() {
        return activities;
    }

    public void setActivity(Map<String, Long> activity) {
        this.activities = activity;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
