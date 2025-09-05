package com.hotel.crock_crest.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "opzioni_personalizzazione")
public class OpzionePersonalizzazione {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idOpzione;

    private String nomeOpzione;
    private Double prezzoAggiuntivo;

    @OneToMany(mappedBy = "opzione", cascade = CascadeType.ALL)
    private List<InventarioCamera> inventario;

    @OneToMany(mappedBy = "opzione", cascade = CascadeType.ALL)
    private List<DettaglioPrenotazione> dettagliPrenotazioni;

    //qua andrebbe int o Long --da vedere
    public int getIdOpzione() {
        return idOpzione;
    }

    public void setIdOpzione(int idOpzione) {
        this.idOpzione = idOpzione;
    }

    public String getNomeOpzione() {
        return nomeOpzione;
    }

    public void setNomeOpzione(String nomeOpzione) {
        this.nomeOpzione = nomeOpzione;
    }

    public Double getPrezzoAggiuntivo() {
        return prezzoAggiuntivo;
    }

    public void setPrezzoAggiuntivo(Double prezzoAggiuntivo) {
        this.prezzoAggiuntivo = prezzoAggiuntivo;
    }

    public List<InventarioCamera> getInventario() {
        return inventario;
    }

    public void setInventario(List<InventarioCamera> inventario) {
        this.inventario = inventario;
    }

    public List<DettaglioPrenotazione> getDettagliPrenotazioni() {
        return dettagliPrenotazioni;
    }

    public void setDettagliPrenotazioni(List<DettaglioPrenotazione> dettagliPrenotazioni) {
        this.dettagliPrenotazioni = dettagliPrenotazioni;
    }
}
