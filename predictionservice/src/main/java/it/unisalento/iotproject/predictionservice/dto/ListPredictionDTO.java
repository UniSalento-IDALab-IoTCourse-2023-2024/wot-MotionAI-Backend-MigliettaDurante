package it.unisalento.iotproject.predictionservice.dto;

import java.util.ArrayList;

public class ListPredictionDTO {
    ArrayList<PredictionDTO> list;

    public ArrayList<PredictionDTO> getList() {
        return list;
    }

    public void setList(ArrayList<PredictionDTO> list) {
        this.list = list;
    }
}
