package it.unisalento.recproject.authenticationservice.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document("user")
public class User {

    @Id
    private String id;
    private String nome;
    private String cognome;
    private String dataNascita;
    private String email;
    private String password;

    public String getPassword() {
        return password;
    }

    public String getDataNascita() {
        return dataNascita;
    }

    public void setDataNascita(String dataNascita) {
        this.dataNascita = dataNascita;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

