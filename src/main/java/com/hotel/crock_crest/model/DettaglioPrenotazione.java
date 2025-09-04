package com.hotel.crock_crest.model;

import jakarta.annotation.Generated;
import jakarta.persistence.*;

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
    private OpzionePersonalizzazione opzionePersonalizzazione;

    // costruttore & getter/setter
    public DettaglioPrenotazione() {}

    public int getIdDettaglio() {
        return idDettaglio;
    }

    public void setIdDettaglio ( int idDettaglio) {
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

    public void setOpzionePersonalizzazione (OpzionePersonalizzazione opzionePersonalizzazione) {
        this.opzionePersonalizzazione = opzionePersonalizzazione;
    }
}
