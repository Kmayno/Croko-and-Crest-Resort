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
@Table(name = "camere")
public class Camera {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idCamera;

    private String numeroStanza;
    private String tipoCamera;
    private String descrizione;

    public int getIdCamera() {
        return idCamera;
    }

    public void setIdCamera(int idCamera) {
        this.idCamera = idCamera;
    }

    public String getNumeroStanza() {
        return numeroStanza;
    }

    public void setNumeroStanza(String numeroStanza) {
        this.numeroStanza = numeroStanza;
    }

    public String getTipoCamera() {
        return tipoCamera;
    }

    public void setTipoCamera(String tipoCamera) {
        this.tipoCamera = tipoCamera;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public Double getPrezzoBaseNotte() {
        return prezzoBaseNotte;
    }

    public void setPrezzoBaseNotte(Double prezzoBaseNotte) {
        this.prezzoBaseNotte = prezzoBaseNotte;
    }

    public Integer getCapienzaMassima() {
        return capienzaMassima;
    }

    public void setCapienzaMassima(Integer capienzaMassima) {
        this.capienzaMassima = capienzaMassima;
    }

    public Boolean getDisponibile() {
        return disponibile;
    }

    public void setDisponibile(Boolean disponibile) {
        this.disponibile = disponibile;
    }

    public List<InventarioCamera> getInventario() {
        return inventario;
    }

    public void setInventario(List<InventarioCamera> inventario) {
        this.inventario = inventario;
    }

    public List<Prenotazione> getPrenotazioni() {
        return prenotazioni;
    }

    public void setPrenotazioni(List<Prenotazione> prenotazioni) {
        this.prenotazioni = prenotazioni;
    }

    private Double prezzoBaseNotte;
    private Integer capienzaMassima;

    private Boolean disponibile;

    @OneToMany(mappedBy = "camera", cascade = CascadeType.ALL)
    private List<InventarioCamera> inventario;

    @OneToMany(mappedBy = "camera", cascade = CascadeType.ALL)
    private List<Prenotazione> prenotazioni;
}