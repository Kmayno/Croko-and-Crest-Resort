package com.hotel.crock_crest.model;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

// Questa classe serve come Data Transfer Object (DTO) per le richieste di creazione di una prenotazione.
public class PrenotazioneRequest {

    private Integer idCliente;
    private Integer idCamera;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataInizio;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataFine;
    
    private List<Integer> opzioniIds;

    // Costruttori
    public PrenotazioneRequest() {}

    public PrenotazioneRequest(Integer idCliente, Integer idCamera, LocalDate dataInizio, LocalDate dataFine, List<Integer> opzioniIds) {
        this.idCliente = idCliente;
        this.idCamera = idCamera;
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
        this.opzioniIds = opzioniIds;
    }

    // Getters e Setters

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public Integer getIdCamera() {
        return idCamera;
    }

    public void setIdCamera(Integer idCamera) {
        this.idCamera = idCamera;
    }

    public LocalDate getDataInizio() {
        return dataInizio;
    }

    public void setDataInizio(LocalDate dataInizio) {
        this.dataInizio = dataInizio;
    }

    public LocalDate getDataFine() {
        return dataFine;
    }

    public void setDataFine(LocalDate dataFine) {
        this.dataFine = dataFine;
    }

    public List<Integer> getOpzioniIds() {
        return opzioniIds;
    }

    public void setOpzioniIds(List<Integer> opzioniIds) {
        this.opzioniIds = opzioniIds;
    }

    @Override
    public String toString() {
        return "PrenotazioneRequest{" +
                "idCliente=" + idCliente +
                ", idCamera=" + idCamera +
                ", dataInizio=" + dataInizio +
                ", dataFine=" + dataFine +
                ", opzioniIds=" + opzioniIds +
                '}';
    }
}
