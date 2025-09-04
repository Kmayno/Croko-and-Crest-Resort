package com.hotel.crock_crest.model;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "clienti")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private int  idCliente;

    private String nome;
    private String cognome;
    private String email;

    @Column(name = "password_cliente")
    private String passwordCliente;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<Prenotazione> prenotazioni;

    public Cliente() {
    }

    // Getters & Setters

    public int getIdCliente() {
        return idCliente;
    }

    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordCliente() {
        return passwordCliente;
    }

    public List<Prenotazione> getPrenotazioni() {
        return prenotazioni;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPasswordCliente(String passwordCliente) {
        this.passwordCliente = passwordCliente;
    }

    public void setPrenotazioni(List<Prenotazione> prenotazioni) {
        this.prenotazioni = prenotazioni;
    }


}
 
