package it.unisalento.recproject.authenticationservice.dto;

import java.util.ArrayList;

public class UsersListDTO {
    private ArrayList<UserDTO> list;



    public ArrayList<UserDTO> getList() {
        return list;
    }

    public void setList(ArrayList<UserDTO> list) {
        this.list = list;
    }
}
