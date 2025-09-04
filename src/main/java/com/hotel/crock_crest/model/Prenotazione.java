package com.hotel.crock_crest.model;

import java.time.LocalDate;
import java.util.List;
import jakarta.persistence.*;


@Entity
@Table(name = "prenotazioni")
public class Prenotazione {
   
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_prenotazione")
    private int idPrenotazione;

    @Column(name ="data_inizio")
    private LocalDate dataInizio;

    @Column(name ="data_fine")
    private LocalDate dataFine;

    @Column(name = "prezzo_totale")
    private double prezzoTotale;

    @Column(name = "stato_prenotazione")
    private Boolean statoPrenotazione;

    @ManyToOne
    @JoinColumn(name ="id_cliente")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "id_camera")
    private Camera camera;

    @OneToMany(mappedBy= "prenotazione", cascade= CascadeType.ALL)
    private List<DettaglioPrenotazione> dettagli;
    
    public Prenotazione();

    public int getIdPrenotazione() {
        return idPrenotazione;
    }

    public void setIdPrenotazione(int idPrenotazione) {
        this.idPrenotazione = idPrenotazione;
    }

    public LocalDate getDataInizio() {
        return dataInizio;
    }

    public void setDataInizio( LocalDate dataInizio) {
        this.dataInizio = dataInizio;
    }

    public LocalDate getDataFine(){
        return dataFine;
    }

    public void setDataFine ( LocalDate dataFine) {
        this.dataFine = dataFine;
    }

    public Double getPrezzoTotale() {
        return prezzoTotale;
    }

    public void setPrezzoTotale( Double prezzoTotale) {
        this.prezzoTotale = prezzoTotale;
    }

    public Boolean getStatoPrenotazione() {
        return statoPrenotazione;
    }

    public void setStatoPrenotazione( Boolean statoPrenotazione) {
        this.statoPrenotazione = statoPrenotazione;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente; 
    }

    public Camera getCamera(){
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public List<DettaglioPrenotazione> getDettagli() {
        return dettagli;
    }

    public void setDettagli(List<DettaglioPrenotazione> dettagli) {
        this.dettagli = dettagli;
    }
}
