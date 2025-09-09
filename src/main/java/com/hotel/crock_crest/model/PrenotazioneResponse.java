package com.hotel.crock_crest.model;

import java.time.LocalDate;

// Questa classe serve come Data Transfer Object (DTO) per le risposte relative a una prenotazione.
public class PrenotazioneResponse {

    private Integer idPrenotazione;
    private LocalDate dataInizio;
    private LocalDate dataFine;
    private double prezzoTotale;
    private boolean statoPrenotazione;
    private Integer idCliente;
    private String nomeCliente;
    private Integer idCamera;
    private String numeroStanza;

    public PrenotazioneResponse(Prenotazione prenotazione) {
        this.idPrenotazione = prenotazione.getIdPrenotazione();
        this.dataInizio = prenotazione.getDataInizio();
        this.dataFine = prenotazione.getDataFine();
        this.prezzoTotale = prenotazione.getPrezzoTotale();
        this.statoPrenotazione = prenotazione.getStatoPrenotazione();
        
        if (prenotazione.getCliente() != null) {
            this.idCliente = prenotazione.getCliente().getIdCliente();
            this.nomeCliente = prenotazione.getCliente().getNome() + " " + prenotazione.getCliente().getCognome();
        }

        if (prenotazione.getCamera() != null) {
            this.idCamera = prenotazione.getCamera().getIdCamera();
            this.numeroStanza = prenotazione.getCamera().getNumeroStanza();
        }
    }

    // Getters

    public Integer getIdPrenotazione() {
        return idPrenotazione;
    }

    public LocalDate getDataInizio() {
        return dataInizio;
    }

    public LocalDate getDataFine() {
        return dataFine;
    }

    public double getPrezzoTotale() {
        return prezzoTotale;
    }

    public boolean isStatoPrenotazione() {
        return statoPrenotazione;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public Integer getIdCamera() {
        return idCamera;
    }

    public String getNumeroStanza() {
        return numeroStanza;
    }
}
