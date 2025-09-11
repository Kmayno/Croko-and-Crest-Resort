package com.hotel.crock_crest.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name ="dettagli_prenotazione")
public class DettaglioPrenotazione {
    // dati tabella
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_dettaglio")
    private int idDettaglio;

    @ManyToOne
    @JoinColumn(name = "id_prenotazione")
    private Prenotazione prenotazione;

    @ManyToOne
    @JoinColumn(name = "id_opzione")
    @JsonBackReference
    private OpzionePersonalizzazione opzionePersonalizzazione;

    // costruttore & getter/setter

    public int getIdDettaglio() {
        return idDettaglio;
    }

    public void setIdDettaglio(int idDettaglio) {
        this.idDettaglio = idDettaglio;
    }

    public Prenotazione getPrenotazione() {
        return prenotazione;
    }

    public void setPrenotazione(Prenotazione prenotazione) {
        this.prenotazione = prenotazione;
    }

    public OpzionePersonalizzazione getOpzionePersonalizzazione() {
        return opzionePersonalizzazione;
    }

    public void setOpzionePersonalizzazione(OpzionePersonalizzazione opzionePersonalizzazione) {
        this.opzionePersonalizzazione = opzionePersonalizzazione;
    }
   
}
